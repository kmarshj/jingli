package com.zdht.jingli.groups.event;

import com.zdht.jingli.groups.AndroidEventManager;
import com.zdht.jingli.groups.EventCode;
import com.zdht.jingli.groups.URLUtils;
import com.zdht.jingli.groups.model.FacultyClassesStructure;
import com.zdht.jingli.groups.parampool.DBHandleType;

public class GetFacultyStructureEvent extends HttpGetEvent {
	
	private String mDescribe;
	private FacultyClassesStructure facultyClassesStructure;
	private boolean		mUpdated;

	public GetFacultyStructureEvent(int nEventCode) {
		super(nEventCode);
		mIsNeedAddUser = true;
	}

	@Override
	public Object getReturnParam() {
		return facultyClassesStructure;
	}
	
	
	
	@Override
	public void run(Object... params) throws Exception {
		if(facultyClassesStructure == null){
			DBFacultyClassesStructureEvent dbEvent = (DBFacultyClassesStructureEvent)AndroidEventManager.getInstance()
					.runEvent(EventCode.DB_FacultyClassesStructureHandle, 
							DBHandleType.READ);
			
			final String strJson = dbEvent.getJsonData();
			final String strSign = dbEvent.getSign();
			if(strJson != null){
				facultyClassesStructure = new FacultyClassesStructure(strJson);
			}else{
				mUpdated = false;
			}
			
			if(!mUpdated){
				String strUrl = String.format(URLUtils.URL_GetFaculty,
						strSign == null ? "1" : strSign);
				
				super.run(strUrl);
				
				if(isRequestSuccess()){
					facultyClassesStructure = new FacultyClassesStructure(getHttpRetString());
					AndroidEventManager.getInstance().runEvent(EventCode.DB_FacultyClassesStructureHandle,
							DBHandleType.WRITE,
							facultyClassesStructure.getmSign(),
							getHttpRetString());
					mUpdated = true;
				}
			}
		}
	}

	public String getmDescribe() {
		return mDescribe;
	}

	
	
	
	
	
}
