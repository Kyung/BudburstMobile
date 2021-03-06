package edu.ucla.cens.budburstmobile.helper;

public class HelperPlantItem implements Cloneable{
	
	private String mCommonName;
	private String mSpeciesName;
	private String mImageName;
	private String mSiteName;
	private String mPhenoName;
	private String mDescription;
	private String mImageUrl;
	private String mNote;
	private String mCredit;
	private String mDate;
	private String mUserName;
	
	private int mImageID;
	private int mSynced;
	private int mCategory;
	private int mWhichList;
	private int mWhere;
	private int mPicture;
	private int mSpeciesID;
	private int mProtocolID;
	private int mPhenoID;
	private int mPhenoImageID;
	private int mSiteID;
	private int mCurrentPheno;
	private int mTotalPheno;
	private int mPlantID;
	private int mOneTimePlantID;
	private int mFloracacheID;
	private int mIsFloracache;
	
	private double mLatitude;
	private double mLongitude;
	private float mDistance;
	
	private boolean mHeader;
	private boolean mTopItem;
	private boolean mMonitor;
	private boolean mFlag;
	
	public HelperPlantItem clone() throws CloneNotSupportedException {
		HelperPlantItem pItem = (HelperPlantItem)super.clone();
		
		return pItem;
	}
	
	
	public void setCommonName(String commonName) {
		mCommonName = commonName;
	}
	
	public String getCommonName() {
		return mCommonName;
	}
	
	public void setSpeciesName(String speciesName) {
		mSpeciesName = speciesName;
	}
	
	public String getSpeciesName() {
		return mSpeciesName;
	}
	
	public void setImageName(String imageName) {
		mImageName = imageName;
	}
	
	public String getImageName() {
		return mImageName;
	}
	
	public void setSiteName(String siteName) {
		mSiteName = siteName;
	}
	
	public String getSiteName() {
		return mSiteName;
	}
	
	public void setPhenoName(String phenoName) {
		mPhenoName = phenoName;
	}
	
	public String getPhenoName() {
		return mPhenoName;
	}
	
	public void setDescription(String description) {
		mDescription = description;
	}
	
	public String getDescription() {
		return mDescription;
	}
	
	public void setImageURL(String imageUrl) {
		mImageUrl = imageUrl;
	}
	
	public String getImageURL() {
		return mImageUrl;
	}
	
	public void setNote(String note) {
		mNote = note;
	}
	
	public String getNote() {
		return mNote;
	}
	
	public void setCredit(String credit) {
		mCredit = credit;
	}
	
	public String getCredit() {
		return mCredit;
	}
	
	public void setDate(String date) {
		mDate = date;
	}
	
	public String getDate() {
		return mDate;
	}
	
	public void setUserName(String userName) {
		mUserName = userName;
	}
	
	public String getUserName() {
		return mUserName;
	}
	
	public void setImageID(int imageID) {
		mImageID = imageID;
	}
	
	public int getImageID() {
		return mImageID;
	}
	
	public void setWhichList(int whichList) {
		mWhichList = whichList;
	}
	
	public int getWhichList() {
		return mWhichList;
	}
	
	public void setWhere(int where) {
		mWhere = where;
	}
	
	public int getWhere() {
		return mWhere;
	}
	
	public void setPicture(int picture) {
		mPicture = picture;
	}
	
	public int getPicture() {
		return mPicture;
	}
	
	public void setSpeciesID(int speciesID) {
		mSpeciesID = speciesID;
	}
	
	public int getSpeciesID() {
		return mSpeciesID;
	}
	
	public void setPlantID(int plantID) {
		mPlantID = plantID;
	}
	
	public int getPlantID() {
		return mPlantID;
	}
	
	public void setProtocolID(int protocolID) {
		mProtocolID = protocolID;
	}
	
	public int getProtocolID() {
		return mProtocolID;
	}
	
	public void setPhenoID(int phenoID) {
		mPhenoID = phenoID;
	}
	
	public int getPhenoID() {
		return mPhenoID;
	}
	
	public void setPhenoImageID(int phenoImageID) {
		mPhenoImageID = phenoImageID;
	}
	
	public int getPhenoImageID() {
		return mPhenoImageID;
	}

	public void setSiteID(int siteID) {
		mSiteID = siteID;
	}
	
	public int getSiteID() {
		return mSiteID;
	}
	
	public void setCurrentPheno(int currentPheno) {
		mCurrentPheno = currentPheno;
	}
	
	public int getCurrentPheno() {
		return mCurrentPheno;
	}
	
	public void setTotalPheno(int totalPheno) {
		mTotalPheno = totalPheno;
	}
	
	public int getTotalPheno() {
		return mTotalPheno;
	}
	
	public void setSynced(int synced) {
		mSynced = synced;
	}
	
	public int getSynced() {
		return mSynced;
	}
	
	public void setOneTimePlantID(int oneTimePlantID) {
		mOneTimePlantID = oneTimePlantID;
	}
	
	public int getOneTimePlantID() {
		return mOneTimePlantID;
	}
	
	public void setCategory(int category) {
		mCategory = category;
	}
	
	public int getCategory() {
		return mCategory;
	}
	
	public void setLatitude(double latitude) {
		mLatitude = latitude;
	}
	
	public double getLatitude() {
		return mLatitude;
	}
	
	public void setLongitude(double longitude) {
		mLongitude = longitude;
	}
	
	public double getLongitude() {
		return mLongitude;
	}
	
	public void setHeader(boolean header) {
		mHeader = header;
	}
	
	public boolean getHeader() {
		return mHeader;
	}

	public void setTopItem(boolean topItem) {
		mTopItem = topItem;
	}
	
	public boolean getTopItem() {
		return mTopItem;
	}
	
	public void setMonitor(boolean monitor) {
		mMonitor = monitor;
	}
	
	public boolean getMonitor() {
		return mMonitor;
	}
	
	public void setFlag(boolean flag) {
		mFlag = flag;
	}
	
	public boolean getFlag() {
		return mFlag;
	}
	
	public void setDistance(float distance) {
		mDistance = distance;
	}
	
	public float getDistance() {
		return mDistance;
	}
	
	public void setFloracacheID(int floacacheID) {
		mFloracacheID = floacacheID;
	}
	
	public int getFloracacheID() {
		return mFloracacheID;
	}
	
	public void setIsFloracache(int isFloracache) {
		mIsFloracache = isFloracache;
	}
	
	public int getIsFloracache() {
		return mIsFloracache;
	}
	
	public HelperPlantItem() {
		
	}
	
}