/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dataverse.makedatacount;

import edu.harvard.iq.dataverse.DataFile;
import edu.harvard.iq.dataverse.DatasetVersion;
import edu.harvard.iq.dataverse.DataverseRequestServiceBean;
import edu.harvard.iq.dataverse.FileMetadata;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author matthew
 */
public class MakeDataCountEntry {
    
    private String eventTime;
    private String clientIp;
    private String sessionCookieId;
    private String userCookieId;
    private String userId;
    private String requestUrl;
    private String identifier;
    private String filename;
    private String size;
    private String userAgent;
    private String title;
    private String publisher;
    private String publisherId;
    private String authors;
    private String publicationDate;
    private String version;
    private String otherId;
    private String targetUrl;
    private String publicationYear;
    
    private boolean isPublished;
    public MakeDataCountEntry() {
        //If you are creating one of these from scratch, assume the entry is published
        isPublished = true; 
    }
    
    public MakeDataCountEntry(FacesContext fc, DataverseRequestServiceBean dvRequestService, DatasetVersion publishedVersion) {
        if(fc != null) {
            HttpServletRequest req = (HttpServletRequest)fc.getExternalContext().getRequest();
            setRequestUrl(String.valueOf(req.getRequestURL().append("?").append(req.getQueryString())));
            setTargetUrl(String.valueOf(req.getRequestURL().append("?").append(req.getQueryString())));
            setUserAgent(req.getHeader("user-agent")); 
        }
        
        if(publishedVersion != null) {
            isPublished = true;
            setIdentifier(publishedVersion.getDataset().getGlobalId().asString());
            setAuthors(publishedVersion.getAuthorsStr(false).replace(";", "|"));
            setPublisher(publishedVersion.getRootDataverseNameforCitation());
            setTitle(publishedVersion.getTitle());
            setVersion(String.valueOf(publishedVersion.getVersionNumber()));
            setPublicationYear(new SimpleDateFormat("yyyy").format(publishedVersion.getReleaseTime()));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");     
                format.setTimeZone(TimeZone.getTimeZone("GMT"));
                setPublicationDate(format.format(publishedVersion.getReleaseTime()));
        }
        
        if(dvRequestService != null) {
            setClientIp(String.valueOf(dvRequestService.getDataverseRequest().getSourceAddress()));
            setUserId(dvRequestService.getDataverseRequest().getUser().getIdentifier());
        }
        
        setEventTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Timestamp(new Date().getTime())));
        
        /* Still needed: */
        //setOtherId();
        //setPublisherId();
        //setSessionCookieId();
        //setUesrCookieId();
    }
    
    //This version of the constructor is for the downloads tracked in FileDownloadServiceBean
    //Technically you should be able to get to publishedVersion via the data file, but guestbook's datafile doesn't have that info
    public MakeDataCountEntry(FacesContext fc, DataverseRequestServiceBean dvRequestService, DatasetVersion publishedVersion, DataFile df) {
        //Passing null to the base constructor creates an entry without most of the data
        // which we then prune out before logging. 
        this(fc, dvRequestService, publishedVersion);
        
        setFilename(df.getStorageIdentifier()); //fileMetadata is 0 in some cases...
        setSize(String.valueOf(df.getFilesize())); //Need to probably be massaged into a better format
    }    
    
    //Exception thrown if no published metadata exists for DataFile
    public MakeDataCountEntry(FacesContext fc, DataverseRequestServiceBean dvRequestService, DataFile df) throws UnsupportedOperationException {
        //Passing null to the base constructor creates an entry without most of the data
        // which we then prune out before logging. 
        this(fc, dvRequestService, df.getLatestPublishedFileMetadata().getDatasetVersion());
        
        setFilename(df.getStorageIdentifier()); //fileMetadata is 0 in some cases...
        setSize(String.valueOf(df.getFilesize())); //Need to probably be massaged into a better format
    }
  
    
////MAD: FileMetadata from FileDownloadServiceBean is often null so I don't know if this is the best path
////        It seemed maybe nessecary when trying to get the filename but it looks like Make Data Count never uses this anyways
    
//    public MakeDataCountEntry(FacesContext fc, DataverseRequestServiceBean dvRequestService, FileMetadata fm) {
//        this(fc, dvRequestService, fm.getDatasetVersion());
//        
//        //setFilename(fm.getLabel()); //This is disabled as getting the filename is painful and it seems Make Data Count doesn't use this at all -MAD 4.10
//        setSize(String.valueOf(fm.getDataFile().getFilesize())); //Need to probably be massaged into a better format
//    }
    
    
    
    //MAD: Maybe expand this
    //Checks if some basic entries exist before logging the activity.
    // Also confirms the entry isPublished;
    boolean isValidForLogging() {
        return true;
        //return isPublished;
    }
    
    @Override
    public String toString() {
        return  getEventTime() + "\t" +
                getClientIp() + "\t" +
                getSessionCookieId() + "\t" +
                getUserCookieId() + "\t" +
                getUserId() + "\t" +
                getRequestUrl() + "\t" +
                getIdentifier() + "\t" +
                getFilename() + "\t" +
                getSize() + "\t" +
                getUserAgent() + "\t" +
                getTitle() + "\t" +
                getPublisher() + "\t" +
                getPublisherId() + "\t" +
                getAuthors() + "\t" +
                getPublicationDate() + "\t" +
                getVersion() + "\t" +
                getOtherId() + "\t" +
                getTargetUrl() + "\t" +
                getPublicationYear() + "\n";
    }
    
    /**
     * @return the eventTime
     */
    public String getEventTime() {
        if(eventTime == null) {
            return "-";
        }
        return eventTime;
    }

    /**
     * @param eventTime the eventTime to set
     */
    public final void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    /**
     * @return the clientIp
     */
    public String getClientIp() {
        if(clientIp == null) {
            return "-";
        }
        return clientIp;
    }

    /**
     * @param clientIp the clientIp to set
     */
    public final void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    /**
     * @return the sessionCookieId
     */
    public String getSessionCookieId() {
        if(sessionCookieId == null) {
            return "-";
        }
        return sessionCookieId;
    }

    /**
     * @param sessionCookieId the sessionCookieId to set
     */
    public void setSessionCookieId(String sessionCookieId) {
        this.sessionCookieId = sessionCookieId;
    }

    /**
     * @return the uesrCookieId
     */
    public String getUserCookieId() {
        if(userCookieId == null) {
            return "-";
        }
        return userCookieId;
    }

    /**
     * @param uesrCookieId the uesrCookieId to set
     */
    public void setUserCookieId(String uesrCookieId) {
        this.userCookieId = uesrCookieId;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        if(userId == null) {
            return "-";
        }
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public final void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the requestUrl
     */
    public String getRequestUrl() {
        if(requestUrl == null) {
            return "-";
        }
        return requestUrl;
    }

    /**
     * @param requestUrl the requestUrl to set
     */
    public final void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    /**
     * @return the identifier
     */
    public String getIdentifier() {
        if(identifier == null) {
            return "-";
        }
        return identifier;
    }

    /**
     * @param identifier the identifier to set
     */
    public final void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        if(filename == null) {
            return "-";
        }
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public final void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return the size
     */
    public String getSize() {
        if(size == null) {
            return "-";
        }
        return size;
    }

    /**
     * @param size the size to set
     */
    public final void setSize(String size) {
        this.size = size;
    }

    /**
     * @return the userAgent
     */
    public String getUserAgent() {
        if(userAgent == null) {
            return "-";
        }
        return userAgent;
    }

    /**
     * @param userAgent the userAgent to set
     */
    public final void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        if(title == null) {
            return "-";
        }
        return title;
    }

    /**
     * @param title the title to set
     */
    public final void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the publisher
     */
    public String getPublisher() {
        if(publisher == null) {
            return "-";
        }
        return publisher;
    }

    /**
     * @param publisher the publisher to set
     */
    public final void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * @return the publisherId
     */
    public String getPublisherId() {
        if(publisherId == null) {
            return "-";
        }
        return publisherId;
    }

    /**
     * @param publisherId the publisherId to set
     */
    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    /**
     * @return the authors
     */
    public String getAuthors() {
        if(authors == null) {
            return "-";
        }
        return authors;
    }

    /**
     * @param authors the authors to set
     */
    public final void setAuthors(String authors) {
        this.authors = authors;
    }

    /**
     * @return the publicationDate
     */
    public String getPublicationDate() {
        if(publicationDate == null) {
            return "-";
        }
        return publicationDate;
    }

    /**
     * @param publicationDate the publicationDate to set
     */
    public final void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        if(version == null) {
            return "-";
        }
        return version;
    }

    /**
     * @param version the version to set
     */
    public final void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the otherId
     */
    public String getOtherId() {
        if(otherId == null) {
            return "-";
        }
        return otherId;
    }

    /**
     * @param otherId the otherId to set
     */
    public void setOtherId(String otherId) {
        this.otherId = otherId;
    }

    /**
     * @return the targetUrl
     */
    public String getTargetUrl() {
        if(targetUrl == null) {
            return "-";
        }
        return targetUrl;
    }

    /**
     * @param targetUrl the targetUrl to set
     */
    public final void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    /**
     * @return the publicationYear
     */
    public String getPublicationYear() {
        if(publicationYear == null) {
            return "-";
        }
        return publicationYear;
    }

    /**
     * @param publicationYear the publicationYear to set
     */
    public final void setPublicationYear(String publicationYear) {
        this.publicationYear = publicationYear;
    }

}
