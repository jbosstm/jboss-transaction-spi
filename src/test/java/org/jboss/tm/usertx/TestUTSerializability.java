/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.tm.usertx;

import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;
import jakarta.transaction.TransactionManager;
import org.jboss.tm.usertx.client.ServerVMClientUserTransaction;
import org.junit.Test;

//import jakarta.transaction.*;
import java.io.*;

import static org.junit.Assert.*;

/**
 * Note that the majority of the spi tests are in the narayana repo (<a href="https://github.com/jbosstm/narayana.git">...</a>)
 */
public class TestUTSerializability {
    @Test
    public void serializeUTTest() throws IOException, ClassNotFoundException, SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        TransactionManager tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
        ServerVMClientUserTransaction ut;

        assertNotNull(tm);
        ut = new ServerVMClientUserTransaction(tm); //ServerVMClientUserTransaction.getSingleton();

        UserTransactionRegistry utr = new UserTransactionRegistry();

        ut.setTransactionRegistry(utr);

        ut.begin();
        ut.commit();

        // validate that ut can be serialized
        byte[] serializedForm = serialize(ut);

        // validate that ut can be deserialized
        ServerVMClientUserTransaction ut2 = (ServerVMClientUserTransaction) deserialize(serializedForm);

        // validate that the deserialized form is the same as the original
        assertEquals(ut, ut2);
    }

    private byte[] serialize(Object object) throws IOException {
        // no try with resources for the supported lang level (1.6)

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutput out = new ObjectOutputStream(bos)) {

            out.writeObject(object);

            return bos.toByteArray();
        }
    }

    private Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        // no try with resources for the supported lang level (1.6)

        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInput in = new ObjectInputStream(bis)) {

            return in.readObject();
        }
    }
}
