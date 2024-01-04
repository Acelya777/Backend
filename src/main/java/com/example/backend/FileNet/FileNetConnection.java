package com.example.backend.FileNet;



import com.filenet.api.admin.DatabaseStorageArea;
import com.filenet.api.admin.StoragePolicy;
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.DocumentSet;
import com.filenet.api.collection.FolderSet;
import com.filenet.api.collection.ReferentialContainmentRelationshipSet;
import com.filenet.api.constants.*;
import com.filenet.api.core.*;

import com.filenet.api.events.UpdateEvent;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.Properties;
import com.filenet.api.property.Property;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.util.Id;
import com.filenet.api.util.UserContext;

import com.filenet.apiimpl.core.DatabaseStorageAreaImpl;
import org.springframework.stereotype.Service;



import javax.security.auth.Subject;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

import static com.filenet.api.constants.ReservationType.EXCLUSIVE;

@Service
public class FileNetConnection {
   /* @Value("${app.url}")
    String APP_URL;
    @Value("${app.filenet.username}")
    String FILENET_USERNAME;
    @Value("${app.filenet.password}")
    String FILENET_PASSWORD;
    @Value("${app.stanza}")
    String FILENET_STANZA;
    @Value("${app.obst}")
    String APP_OBST;*/

    public static ObjectStore Connection() throws Exception {
        try {
            //http://192.168.220.203:9081/acce/
            //app.url=http://192.168.220.203:9081/wsi/FNCEWS40MTOM/
            //app.stanza=FileNetP8WSI
            //app.obst=OBST
            //app.filenet.username=cpeadmin
            //app.filenet.password=Bbs@2019
            //app.ldap.url=192.168.220.223

            com.filenet.api.core.Connection conn = Factory.Connection.getConnection("http://192.168.220.203:9081/wsi/FNCEWS40MTOM/");

            Subject sub = UserContext.createSubject(conn, "cpeadmin", "Bbs@2019", "FileNetP8WSI");

            UserContext uc = UserContext.get();

            uc.pushSubject(sub);

            Domain dom = Factory.Domain.getInstance(conn, null);

            ObjectStore os = Factory.ObjectStore.fetchInstance(dom, "OBST", null);

            return os;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        }
    }

    public Boolean CreateDynamic(String base64Data, User user) throws EngineRuntimeException, Exception {

        try {

            ObjectStore os = Connection();

            Document doc = Factory.Document.createInstance(os, "doc_AcelyaTestKimlik", null);

            doc.getProperties().putValue("DocumentTitle", user.getDocumentTitle());
            doc.getProperties().putValue("prp_Ad", user.getFirstName());
            doc.getProperties().putValue("prp_soyisim", user.getLastName());
            doc.getProperties().putValue("prp_tc", user.getTC());
            doc.getProperties().putValue("prp_IrtibatKisisiEmail", user.getMail());

            doc.save(RefreshMode.NO_REFRESH);

            doc.save(RefreshMode.REFRESH);


            ContentTransfer ct = Factory.ContentTransfer.createInstance();

            byte[] myByteArray = Base64.getDecoder().decode(base64Data);
            ct.setCaptureSource(new ByteArrayInputStream(myByteArray));

            ct.set_ContentType("application/pdf");

            ct.set_RetrievalName("acelyaDeneme");

            ContentElementList cel = Factory.ContentElement.createList();

            cel.add(ct);
            doc.set_ContentElements(cel);

            doc.save(RefreshMode.REFRESH);

            doc.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);

            doc.save(RefreshMode.REFRESH);

            Folder folder = Factory.Folder.getInstance(os, ClassNames.FOLDER, "/Acelya Test Documents/");

            ReferentialContainmentRelationship rcr = folder.file(doc, AutoUniqueName.AUTO_UNIQUE, "Acelya Test Document",
                    DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);

            rcr.save(RefreshMode.NO_REFRESH);

            return true;
        } catch (EngineRuntimeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

    }

    public static User fetchAndPrintDocumentProperties(String documentId) {

        try {
            ObjectStore os = Connection();

            PropertyFilter pf = new PropertyFilter();

            pf.addIncludeProperty(new FilterElement(null, null, null, "DocumentTitle", null));
            pf.addIncludeProperty(new FilterElement(null, null, null, "prp_Ad", null));
            pf.addIncludeProperty(new FilterElement(null, null, null, "prp_soyisim", null));
            pf.addIncludeProperty(new FilterElement(null, null, null, "prp_tc", null));
            pf.addIncludeProperty(new FilterElement(null, null, null, "prp_IrtibatKisisiEmail", null));
            pf.addIncludeProperty(new FilterElement(null, null, null, "Id", null));


            Document doc = Factory.Document.fetchInstance(os, new Id(documentId), pf);


            com.filenet.api.property.Properties props = doc.getProperties();

            User user = new User();
            Iterator iter = props.iterator();


            while (iter.hasNext()) {
                Property prop = (Property) iter.next();
                if (prop.getPropertyName().equals("DocumentTitle"))
                    user.setDocumentTitle(prop.getPropertyName());
                else if (prop.getPropertyName().equals("prp_Ad"))
                    user.setFirstName(prop.getStringValue());
                else if (prop.getPropertyName().equals("prp_soyisim"))
                    user.setLastName(prop.getStringValue());
                else if (prop.getPropertyName().equals("prp_tc"))
                    user.setTC(prop.getStringValue());
                else if (prop.getPropertyName().equals("prp_IrtibatKisisiEmail"))
                    user.setMail(prop.getStringValue());
                else if (prop.getPropertyName().equals("Id"))
                    user.setId(prop.getIdValue());
            }
            return user;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static List<User> listAllDocumentsInFolder(String folderId) {
        List<User> userList = new ArrayList<>();
        try {
            ObjectStore os = Connection();

            Folder folder = Factory.Folder.fetchInstance(os, new Id(folderId), null);
            DocumentSet docSet = folder.get_ContainedDocuments();

            Iterator<?> iter = docSet.iterator();

            while (iter.hasNext()) {
                Document doc = (Document) iter.next();

                User user = fetchAndPrintDocumentProperties(String.valueOf(doc.get_Id()));
                userList.add(user);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return userList;
    }

    public static void DeleteFile(String documentId) {
        try {
            ObjectStore os = Connection();
            Document doc = Factory.Document.getInstance(os, ClassNames.DOCUMENT, new Id(documentId));
            doc.delete();
            doc.save(RefreshMode.NO_REFRESH);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void UpdateFile(String documentId, User user) {
        try {
            ObjectStore os = Connection();
            PropertyFilter pf = new PropertyFilter();
            pf.addIncludeProperty(new FilterElement(null, null, null, "DocumentTitle", null));
            pf.addIncludeProperty(new FilterElement(null, null, null, "prp_Ad", null));
            pf.addIncludeProperty(new FilterElement(null, null, null, "prp_soyisim", null));
            pf.addIncludeProperty(new FilterElement(null, null, null, "prp_tc", null));
            pf.addIncludeProperty(new FilterElement(null, null, null, "prp_IrtibatKisisiEmail", null));
            Document doc = Factory.Document.fetchInstance(os, new Id(documentId), pf);


            Properties props = doc.getProperties();
            props.putValue("DocumentTitle", user.getDocumentTitle());
            props.putValue("prp_Ad", user.getFirstName());
            props.putValue("prp_soyisim", user.getLastName());
            props.putValue("prp_tc", user.getTC());
            props.putValue("prp_IrtibatKisisiEmail", user.getMail());


            doc.save(RefreshMode.REFRESH);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream SetDocumentFile(String documentId) {
        try {
            ObjectStore os = Connection();

            Document doc = Factory.Document.fetchInstance(os, new Id(documentId), null);
            ContentElementList contentElements = doc.get_ContentElements();
            if (contentElements != null && !contentElements.isEmpty()) {
                ContentTransfer contentTransfer = (ContentTransfer) contentElements.get(0);
                return contentTransfer.accessContentStream();
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }


    public static void ChangeFileVersion(String base64Data, String folderId, User user) {
        try {
            ObjectStore os = Connection();

            Document doc = Factory.Document.getInstance(os, ClassNames.DOCUMENT, new Id(folderId));


            doc.getProperties().putValue("DocumentTitle", user.getDocumentTitle());
            doc.getProperties().putValue("prp_Ad", user.getFirstName());
            doc.getProperties().putValue("prp_soyisim", user.getLastName());
            doc.getProperties().putValue("prp_tc", user.getTC());
            doc.getProperties().putValue("prp_IrtibatKisisiEmail", user.getMail());

            doc.save(RefreshMode.NO_REFRESH);

            doc.save(RefreshMode.REFRESH);

            doc.checkout(EXCLUSIVE, null, doc.getClassName(), doc.getProperties());
            doc.save(RefreshMode.REFRESH);

            Document reservation = (Document) doc.get_Reservation();

            try {
                ContentTransfer ctObject = Factory.ContentTransfer.createInstance();
                ContentElementList contentList = Factory.ContentTransfer.createList();


                byte[] myByteArray = Base64.getDecoder().decode(base64Data);
                ctObject.setCaptureSource(new ByteArrayInputStream(myByteArray));
                contentList.add(ctObject);

                reservation.set_ContentElements(contentList);
                reservation.save(RefreshMode.REFRESH);


            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            reservation.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
            reservation.save(RefreshMode.REFRESH);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void moveDocumentContent(String documentId,String targetFolderPath, String targetFolderName) {
        try {
            ObjectStore os = Connection();
            // Fetch the Document
            System.out.println(targetFolderPath);
            System.out.println(targetFolderName);
            Document doc = Factory.Document.getInstance(os, ClassNames.DOCUMENT, new Id(documentId));

            Folder folder = Factory.Folder.getInstance(os, ClassNames.FOLDER, targetFolderPath);

            ReferentialContainmentRelationship rcr = folder.file(doc, AutoUniqueName.AUTO_UNIQUE, targetFolderName,
                    DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);

            rcr.save(RefreshMode.NO_REFRESH);


            System.out.println("Document content moved successfully.");
        } catch (Exception e) {
            System.out.println("Error while moving document content: " + e.getMessage());
        }

    }

        public static List<ExistingFolder> listAllDocuments(String folderId) {
        List<ExistingFolder> folderList = new ArrayList<>();
        try {
            ObjectStore os = Connection();

            Folder folder = Factory.Folder.fetchInstance(os, new Id(folderId), null);
            FolderSet folderSet = folder.get_SubFolders();

            Iterator<?> iter = folderSet.iterator();

            while (iter.hasNext()) {
                Folder folder1 = (Folder) iter.next();

                String folderName = folder1.get_FolderName();

                ExistingFolder existingFolder = new ExistingFolder(folder1.get_Id().toString(), folderName);
                folderList.add(existingFolder);

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return folderList;
    }

}

   /* public static void listDocumentPropertiesAndContentByTC(String tc) throws Exception {
        ObjectStore os = Connection();

        Folder folder = Factory.Folder.getInstance(os, ClassNames.FOLDER, "/Acelya Test Documents/");




        Iterator<?> documentIterator = folder.get_ContainedDocuments().iterator();

        while (documentIterator.hasNext()) {

            Document doc = (Document) documentSet.iterator().next();

            PropertyFilter pf = new PropertyFilter();
            pf.addIncludeProperty(new FilterElement(null, null, null, "DocumentTitle", null));
            pf.addIncludeProperty(new FilterElement(null, null, null, PropertyNames.MIME_TYPE, null));

            doc.refresh(pf);

            Properties props = doc.getProperties();

            String tcPropertyValue = props.getStringValue("prp_tc");

            if (tc.equals(tcPropertyValue)) {
                System.out.println("Document found with TC: " + tc);

                System.out.println("Property" + "\t" + "Value");
                System.out.println("------------------------");

                Iterator iter = props.iterator();
                while (iter.hasNext()) {
                    Property prop = (Property) iter.next();
                    System.out.println(prop.getPropertyName() + "\t" + prop.getObjectValue());
                }

                ContentElementList contentList = doc.get_ContentElements();
                if (contentList != null && !contentList.isEmpty()) {
                    ContentTransfer contentTransfer = (ContentTransfer) contentList.get(0);
                    byte[] contentBytes = contentTransfer.get_ContentType().getBytes();
                    String base64Content = Base64.getEncoder().encodeToString(contentBytes);
                    System.out.println("Base64 Content:\n" + base64Content);
                }

                return;
            }
        }

        System.out.println("No document found with TC: " + tc);
    }
}*/
   /* public static void listDocumentPropertiesAndContentByTC(String tc) throws Exception {
        ObjectStore os = Connection();

        SearchSQL searchSQL = new SearchSQL();
        searchSQL.setQueryString("SELECT * FROM doc_AcelyaTestKimlik WHERE prp_tc = '" + tc + "'");
        SearchScope searchScope = new SearchScope(os);

        DocumentSet documentSet = (DocumentSet) searchScope.fetchObjects(searchSQL, null, null, false);

        if (!documentSet.isEmpty()) {
            Document doc = (Document) documentSet.iterator().next();

            PropertyFilter pf = new PropertyFilter();
            pf.addIncludeProperty(new FilterElement(null, null, null, "DocumentTitle", null));
            pf.addIncludeProperty(new FilterElement(null, null, null, PropertyNames.MIME_TYPE, null));

            doc.refresh(pf);

            Properties props = doc.getProperties();

            System.out.println("Property" + "\t" + "Value");
            System.out.println("------------------------");
            Iterator iter = props.iterator();
            while (iter.hasNext()) {
                Property prop = (Property) iter.next();
                System.out.println(prop.getPropertyName() + "\t" + prop.getObjectValue());
            }

            ContentElementList contentList = doc.get_ContentElements();
            if (contentList != null && !contentList.isEmpty()) {
                ContentTransfer contentTransfer = (ContentTransfer) contentList.get(0);
                byte[] contentBytes = contentTransfer.get_ContentType().getBytes();
                String base64Content = Base64.getEncoder().encodeToString(contentBytes);
                System.out.println("Base64 Content:\n" + base64Content);
            }
        } else {
            System.out.println("No document found with TC: " + tc);
        }
    }*/


