package com.tangotab.core.cache;

import java.io.File;
import android.content.Context;
/**
 * This class will store file into Cache.
 * @author Dillip.Lenka
 *
 */
public class FileCache
{
    
    private File cacheDir=null;
    /**
     * Create files in cache directory.
     * @param context
     */
    public FileCache(Context context)
    {     
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"LazyList");
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }
    /**
     * Get the file from the URL
     * @param url
     * @return
     */
    public File getFile(String url)
    {      
        String filename=String.valueOf(url.hashCode());
        File f = new File(cacheDir, filename);
        return f;
        
    }
    /**
     * Clear all the files from cache
     */
    public void clear()
    {
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }

}