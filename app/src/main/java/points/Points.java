package points;

public class Points {
    private long mBasePoint = 1000;
    private long mCurrentPoint = mBasePoint;

    public void init() {
        mCurrentPoint = mBasePoint;
    }
    public long getCurrentPoint() { return mCurrentPoint; }
    public void addPoints(long point) { mCurrentPoint += point; }
    public void minusPoints(long point) {
        mCurrentPoint -= point;
        if(mCurrentPoint <= 0)
            mCurrentPoint = 0;
    }

    public long getLocalPoint() {
        long localPoint = 0;
        return localPoint;
    }
}
