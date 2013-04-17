package com.tangotab.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.tangotab.R;
import com.tangotab.core.cache.FileCache;
import com.tangotab.core.cache.MemoryCache;
/**
 * Class will be used to load the images for offers.
 * 
 * @author dillip.lenka
 *
 */
public class ImageLoader
{
    
    MemoryCache memoryCache=new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService; 
    private boolean resize=false;
    public ImageLoader(Context context)
    {
        fileCache=new FileCache(context);
        executorService=Executors.newFixedThreadPool(5);
    }
    public ImageLoader(Context context,boolean resize)
    {
    	this.resize=resize;
        fileCache=new FileCache(context);
        executorService=Executors.newFixedThreadPool(5);
    }
    final int stub_id=R.drawable.image_bg;
    /**
     * This method will be used in order to display the image.
     * 
     * @param url
     * @param imageView
     */
    public void DisplayImage(String url, ImageView imageView)
    {
        imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        if(bitmap!=null)
            imageView.setImageBitmap(bitmap);
        else
        {
            queuePhoto(url, imageView);
            imageView.setImageResource(stub_id);
        }
    }
    /**
     *   Get photo from image and URL
     * @param url
     * @param imageView
     */
    private void queuePhoto(String url, ImageView imageView)
    {
        PhotoToLoad p=new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }
    /**
     * Get image from the url
     * @param url
     * @return
     */
    private Bitmap getBitmap(String url) 
    {
        File file=fileCache.getFile(url);
        int chunksize = 1024;    
        Bitmap bit = decodeFile(file);
        if(bit!=null)
            return bit;
       
        try {
            Bitmap bitmap=null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            
            FileOutputStream f = new FileOutputStream(file);

            InputStream in = conn.getInputStream();
            
            byte[] buffer = new byte[chunksize];
            int len1 = 0;
            while ( (len1 = in.read(buffer)) != -1 ) {
              f.write(buffer,0, len1);
            }
            f.close();
            in.close();            
            bitmap = decodeFile(file);            
      
            return bitmap;
        } catch (Exception ex)
        {
        	Log.e("Exception:", "Exception occured at the time of retrieving image from the URL");
        	ex.printStackTrace();
           return null;
        }
    }
    /**
     * decodes image and scales it to reduce memory consumption
     * 
     * @param f
     * @return
     */
     private Bitmap decodeFile(File f)
     {
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            
            //Find the correct scale value. It should be the power of 2.
            int REQUIRED_SIZE;
            if(resize){
            	REQUIRED_SIZE=180;	
            }
            else{
            	REQUIRED_SIZE=70;	
            }
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true)
            {
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }
            
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            
            o2.inSampleSize=scale;
          
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        	Log.e("Exception:", "FileNotFoundException occured in decodeFile method");
        }
        return null;
    }
    /**
     * Task for the queue
     * @author dillip.lenka
     *
     */
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public PhotoToLoad(String u, ImageView i){
            url=u; 
            imageView=i;
        }
    }
    
    class PhotosLoader implements Runnable
    {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
        
   
        public void run() {
            if(imageViewReused(photoToLoad))
                return;
            Bitmap bmp=getBitmap(photoToLoad.url);
            memoryCache.put(photoToLoad.url, bmp);
            if(imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
            Activity a=(Activity)photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }
    
    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.imageView);
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }
    /**
     * Used to display bitmap in the UI thread
     * 
     * @author dillip.lenka
     *
     */
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
        public void run()
        {
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap!=null)
                photoToLoad.imageView.setImageBitmap(bitmap);
            else
                photoToLoad.imageView.setImageResource(stub_id);
        }
    }
    /**
     * Clear the Cache
     */
    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

}
