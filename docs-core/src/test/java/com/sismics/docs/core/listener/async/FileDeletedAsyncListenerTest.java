package com.sismics.docs.core.listener.async;

import com.sismics.docs.BaseTransactionalTest;
import com.sismics.docs.core.dao.UserDao;
import com.sismics.docs.core.event.FileDeletedAsyncEvent;
import com.sismics.docs.core.model.jpa.File;
import com.sismics.docs.core.model.jpa.User;
import com.sismics.docs.core.util.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;

import com.sismics.docs.core.dao.DocumentDao;
import com.sismics.docs.core.model.jpa.Document;

public class FileDeletedAsyncListenerTest extends BaseTransactionalTest {

    @Test
    public void updateQuotaSizeKnown() throws Exception {
        User user = createUser("updateQuotaSizeKnown");
        File file = createFile(user, FILE_JPG_SIZE);
        UserDao userDao = new UserDao();
        user = userDao.getById(user.getId());
        user.setStorageCurrent(10_000L);
        userDao.updateQuota(user);

        FileDeletedAsyncListener fileDeletedAsyncListener = new FileDeletedAsyncListener();
        TransactionUtil.commit();
        FileDeletedAsyncEvent event = new FileDeletedAsyncEvent();
        event.setFileSize(FILE_JPG_SIZE);
        event.setFileId(file.getId());
        event.setUserId(user.getId());
        fileDeletedAsyncListener.on(event);
        Assert.assertEquals(userDao.getById(user.getId()).getStorageCurrent(), Long.valueOf(10_000 - FILE_JPG_SIZE));
    }

    @Test
    public void updateQuotaSizeUnknown() throws Exception {
        User user = createUser("updateQuotaSizeUnknown");
        File file = createFile(user, File.UNKNOWN_SIZE);
        UserDao userDao = new UserDao();
        user = userDao.getById(user.getId());
        user.setStorageCurrent(10_000L);
        userDao.updateQuota(user);

        FileDeletedAsyncListener fileDeletedAsyncListener = new FileDeletedAsyncListener();
        TransactionUtil.commit();
        FileDeletedAsyncEvent event = new FileDeletedAsyncEvent();
        event.setFileSize(FILE_JPG_SIZE);
        event.setFileId(file.getId());
        event.setUserId(user.getId());
        fileDeletedAsyncListener.on(event);
        Assert.assertEquals(userDao.getById(user.getId()).getStorageCurrent(), Long.valueOf(10_000 - FILE_JPG_SIZE));
    }

//    @Test
//    public void testGetDocumentCount() throws Exception {
//
//        User user = createUser("testGetDocumentCountUser");
//
//
//        DocumentDao documentDao = new DocumentDao();
//        long initialCount = documentDao.getDocumentCount();
//
//
//        Document document = new Document();
//        document.setUserId(user.getId());
//        document.setTitle("Test Document");
//        document.setDescription("A test document");
//        document.setCreateDate(new java.util.Date());
//        document.setUpdateDate(new java.util.Date());
//
//        documentDao.create(document, user.getId());
//
//
//        TransactionUtil.commit();
//        long newCount = documentDao.getDocumentCount();
//
//        Assert.assertEquals("add1", initialCount + 1, newCount);
//    }

}
