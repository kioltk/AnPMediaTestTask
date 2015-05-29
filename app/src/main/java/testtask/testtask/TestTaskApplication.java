package testtask.testtask;

import android.app.Application;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by Jesus Christ. Amen.
 */
public class TestTaskApplication extends Application {

    private static TestTaskApplication application;

    /**
     *
     * */
    public static TestTaskApplication app() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        // We define image loading, caching in memory and caching on disk for better picture
        // showing performance.
        ImageLoader.getInstance().init(new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.MAX_PRIORITY)
                .memoryCache(new LruMemoryCache(5 * 1024 * 1024))
                .memoryCacheSize(5 * 1024 * 1024)
                .memoryCacheSizePercentage(40)
                .diskCacheSize(100 * 1024 * 1024)
                .diskCacheFileCount(1000)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build());
    }
}