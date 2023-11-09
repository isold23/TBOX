package user;

import points.Points;

public class TBUser {
    private long mTBId;
    private String mTBName;
    private String mEmail;
    private String mMobile;
    private Points mPoints;

    public void init() {
        mPoints.init();
    }
}
