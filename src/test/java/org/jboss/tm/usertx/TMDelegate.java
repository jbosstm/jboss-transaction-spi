package org.jboss.tm.usertx;

import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionManagerImple;
import com.arjuna.ats.jbossatx.BaseTransactionManagerDelegate;

import org.jboss.tm.listener.event.TransactionLifecycleTypeNotSupported;
import org.jboss.tm.listener.event.AssociationLifecycleEventType;
import org.jboss.tm.listener.event.TransactionLifecycleEventType;
import org.jboss.tm.listener.event.TransactionLifecycleEvent;
import org.jboss.tm.listener.event.TransactionLifecycleListener;
import org.jboss.tm.listener.event.TransactionLifecycleListenerRegistry;
import org.jboss.tm.listener.event.VetoingTransactionLifecycleListener;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TMDelegate extends BaseTransactionManagerDelegate implements TransactionLifecycleListenerRegistry {//TransactionListenerRegistry {
    private static final String TXN_LISTENER_MAP_KEY = "__TXN_LISTENERS";
    private static final String VETOABLE_TXN_LISTENER_MAP_KEY = "__VETOABLE_TXN_LISTENERS";

    private static final TransactionManagerImple JTA_TRANSACTION_MANAGER = new TransactionManagerImple() ;

    public TMDelegate() {
        super(JTA_TRANSACTION_MANAGER);
    }

    @Override
    public Transaction getTransaction() throws SystemException {
        return super.getTransaction();
    }

    @Override
    public Transaction suspend() throws SystemException {
        this.notifyListeners(this.getTransaction(), AssociationLifecycleEventType.DISASSOCIATING);

        return super.suspend();
    }

    @Override
    public void resume(Transaction transaction) throws InvalidTransactionException, IllegalStateException, SystemException {
        notifyListeners(transaction, AssociatingLifecycleEventType.ASSOCIATING);

        super.resume(transaction);

        notifyListeners(transaction, AssociationLifecycleEventType.ASSOCIATED);
    }

    @Override
    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
        this.notifyListeners(this.getTransaction(), AssociationLifecycleEventType.DISASSOCIATING);
        super.commit();
    }

    @Override
    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        if (notifyVetoingListeners(getTransaction(), RollingbackLifecycleEventType.ROLLINGBACK))
            throw new SecurityException("Rollback pertion was vetoed");

        notifyListeners(getTransaction(), AssociationLifecycleEventType.DISASSOCIATING);

        super.rollback();
    }

    @Override
    public int getTransactionTimeout() throws SystemException {
        return 0;
    }

    @Override
    public long getTimeLeftBeforeTransactionTimeout(boolean errorRollback) throws RollbackException {
        return 0;
    }

    private void checkTransaction(Transaction transaction) throws TransactionLifecycleTypeNotSupported {
        if (transaction == null)
            throw new NullPointerException(); // we could interpret this as meaning register for all transactions

        if (!(transaction instanceof com.arjuna.ats.jta.transaction.Transaction))
            throw new TransactionLifecycleTypeNotSupported("Unsupported transaction type");
    }

    private void checkIsMap(Object resource) {
        if (!Map.class.isAssignableFrom(resource.getClass())) {
            // another container subsystem has inadvertently used our key
            throw new IllegalStateException("Invalid transaction local resource associated with key");
        }
    }

    @Override
    public <T extends Enum<T>> void addListener(Transaction transaction, TransactionLifecycleListener<T> listener, EnumSet<T> typeFilter) throws TransactionLifecycleTypeNotSupported {
        checkTransaction(transaction);

        registerListener(transaction, listener, typeFilter);

        // if transaction is already associated with the current thread notify this listener
        try {
            if (transaction.equals(getTransaction()) && typeFilter.contains(AssociationLifecycleEventType.ASSOCIATED))
                listener.onTransactionEvent(new TransactionLifecycleEvent(transaction, AssociationLifecycleEventType.ASSOCIATED));
        } catch (SystemException ignore) {
            // no transaction associated so do not trigger the ASSOCIATED callback
        }
    }

    @Override
    public <T extends Enum<T>> void addListener(Transaction transaction, VetoingTransactionLifecycleListener<T> listener, EnumSet<T> typeFilter) throws TransactionLifecycleTypeNotSupported {
        checkTransaction(transaction);

        registerListener(transaction, listener, typeFilter);
    }

    private <S, E extends Enum<E>> void registerListenerTypes(String mapKey, Transaction transaction, S listener, EnumSet<E> typeFilter) {
        com.arjuna.ats.jta.transaction.Transaction txn = (com.arjuna.ats.jta.transaction.Transaction) transaction;
        Object resource;

        // protect against two concurrent listener registrations both trying to create the initial resource entry
        synchronized (transaction) {
            Map<E, Set<S>> lmap;

            resource = txn.getTxLocalResource(mapKey);

            if (resource == null) {
                lmap = new ConcurrentHashMap<>();

                txn.putTxLocalResource(mapKey, lmap);
            } else {
                checkIsMap(resource);

                lmap = (Map<E, Set<S>>) resource;
            }

            for (E t : typeFilter) {
                Set<S> listeners;

                if (lmap.containsKey(t)) {
                    listeners = lmap.get(t);
                } else {
                    listeners = new HashSet<>();
                    lmap.put(t, listeners);
                }

                listeners.add(listener);
            }
        }
    }

    private <E extends Enum<E>> void registerListener(Transaction transaction, VetoingTransactionLifecycleListener<E> listener, EnumSet<E> typeFilter)
    {
        registerListenerTypes(VETOABLE_TXN_LISTENER_MAP_KEY, transaction, listener, typeFilter);
    }

    private <E extends Enum<E>> void registerListener(Transaction transaction, TransactionLifecycleListener<E> listener, EnumSet<E> typeFilter)
    {
        registerListenerTypes(TXN_LISTENER_MAP_KEY, transaction, listener, typeFilter);
    }

    // notify any vetoing listeners for this transaction that there has been an event
    private <E extends Enum<E>> boolean notifyVetoingListeners(Transaction transaction, TransactionLifecycleEventType<E> reason)
    {
        boolean veto = false;

        if (transaction != null) {
            Map<E, Set<VetoingTransactionLifecycleListener<E>>> listeners = getVetoingListeners(transaction, VetoingTransactionLifecycleListener.class);

            if (listeners != null && listeners.containsKey(reason)) {
                TransactionLifecycleEvent<E> event = new TransactionLifecycleEvent<>(transaction, reason);

                for (VetoingTransactionLifecycleListener s : listeners.get(reason))
                    if (s.onVetoableEvent(event))
                        veto = true;
            }
        }

        return veto;
    }

    // notify any listeners for this transaction that there has been an event
    private <E extends Enum<E>> void notifyListeners(Transaction transaction, TransactionLifecycleEventType<E> reason)
    {
        if (transaction != null) {
            Map<E, Set<TransactionLifecycleListener<E>>> listeners = getListeners(transaction, TransactionLifecycleListener.class);

            if (listeners != null && listeners.containsKey(reason)) {
                TransactionLifecycleEvent<E> event = new TransactionLifecycleEvent<>(transaction, reason);

                for (TransactionLifecycleListener s : listeners.get(reason))
                    s.onTransactionEvent(event);
            }
        }
    }

    private <E extends Enum<E>, L> Map<E, Set<L>> getListenersResource(Transaction transaction, String resourceKey) {
        com.arjuna.ats.jta.transaction.Transaction txn = (com.arjuna.ats.jta.transaction.Transaction) transaction;
        Object resource;

        // protect against two concurrent listener registrations both trying to create the initial resource entry
        synchronized (transaction) {
            resource = txn.getTxLocalResource(resourceKey);

            if (resource != null) {
                checkIsMap(resource);

                return (Map<E, Set<L>>) resource;
            }
        }

        return null;
    }

    private <E extends Enum<E>, L> Map<E, Set<L>> getListeners(Transaction transaction, Class<TransactionLifecycleListener> listener)
    {
        return getListenersResource(transaction, TXN_LISTENER_MAP_KEY);
    }

    private <E extends Enum<E>, L> Map<E, Set<L>> getVetoingListeners(Transaction transaction, Class<VetoingTransactionLifecycleListener> listener)
    {
        return getListenersResource(transaction, VETOABLE_TXN_LISTENER_MAP_KEY);
    }
}
