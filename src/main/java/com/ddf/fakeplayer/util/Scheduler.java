package com.ddf.fakeplayer.util;

public class Scheduler {
    /*uint32_t*/int mTotalFrames;
    /*uint32_t*/int mStarvedFrames;
    /*uint32_t*/int mPromotionFrames;
    /*uint32_t*/int mTargetFPS;
    /*uint32_t*/int mEffectiveFPS;
    /*uint32_t*/int mFramesOverBound;
    double mAverageCallbackDuration;
    double mTotalCoroutineDuration;
    /*size_t*/int mTotalRunCoroutines;
    double mCoroutineTimeLimit;
    WorkerPool mCoroutinePool;
    long mNextStarveCheckTime;
    long mThreadID;
}
