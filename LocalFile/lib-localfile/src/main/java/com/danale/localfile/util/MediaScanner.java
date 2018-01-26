package com.danale.localfile.util;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.danale.localfile.bean.Media;
import com.danale.localfile.constant.MediaType;
import com.danale.localfile.util.DateTimeUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by kevin on 9/26/16.
 */

public class MediaScanner {


    public static TreeMap<String, List<Media>> scan(File fileDir, MediaType mediaType) {
        return scan(fileDir, newDateTreeMap(DateTimeUtils.FORMAT_DATE_DOT), mediaType);
    }

    @NonNull
    public static TreeMap<String, List<Media>> newDateTreeMap(String format) {
        return new TreeMap<>(new DateComparator(format));
    }

    public static TreeMap<String, List<Media>> scan(File fileDir, TreeMap<String, List<Media>> map, MediaType mediaType) {
        if (null != fileDir && fileDir.exists() && fileDir.isDirectory()) {
            String[] imageFileNames = scanImage(fileDir);
            groupFile(fileDir, map, imageFileNames, mediaType);
        }
        return map;
    }


    public static void groupFile(File fileDir, TreeMap<String, List<Media>> map, String[] fileNames, MediaType mediaType) {
        if (map == null || fileNames == null) {
            return;
        }

        for (String filename : fileNames) {
            File file = new File(fileDir, filename);
            Media media = new Media(Uri.fromFile(file));
//            media.setMediaType(isRecord(filename) ? MediaType.RECORD : MediaType.IMAGE);
            media.setMediaType(mediaType);
            String dateTime = DateTimeUtils.getDateTime(file.lastModified(), DateTimeUtils.FORMAT_DATE_DOT, null);
            updateGroup(map, dateTime, media);
        }
    }

    public static void loadHistory(TreeMap<String, List<Media>> map, String dateTime, List<File> files, MediaType mediaType) {
        if (map == null || files == null)
            return;
        for (File file :
                files) {
            Media media = new Media(Uri.fromFile(file));
//            media.setMediaType(isRecord(filename) ? MediaType.RECORD : MediaType.IMAGE);
            media.setMediaType(mediaType);
            updateGroup(map, dateTime, media);
        }

    }

    public static void updateGroup(TreeMap<String, List<Media>> map, String dateTime, Media media) {
        List<Media> medias = map.get(dateTime);
        if (medias != null) {
            medias.add(media);
        } else {
            medias = new ArrayList<>();
            medias.add(media);
            map.put(dateTime, medias);
        }
    }


    private static String[] scanImage(File fileDir) {
        String[] imageFileNames = fileDir.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {
                if(ignoreEapilMiniPic(filename)){
                    return false;
                }else{
                    return isImage(filename);
                }
            }
        });
        return imageFileNames;
    }

    public static boolean isImage(String filename) {
        return filename.toLowerCase().endsWith(".png") || filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg");
    }

    public static boolean ignoreEapilMiniPic(String filename){
        if(!TextUtils.isEmpty(filename) && filename.contains("mini")){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isRecord(String filename) {
        return filename.toLowerCase().endsWith(".mp4");
    }

    private static String[] scanRecord(File fileDir) {
        String[] recordFileNames = fileDir.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String filename) {
                return isRecord(filename);
            }
        });

        return recordFileNames;
    }


    /**
     * 时间倒序
     */
    static class DateComparator implements Comparator {

        String format ;
        DateComparator(String format){
            this.format = format;
        }
        @Override
        public int compare(Object lhs, Object rhs) {
            if (DateTimeUtils.convertStringTimeToLong((String) lhs, format) > DateTimeUtils.convertStringTimeToLong((String) rhs, format)) {
                return -1;
            } else if (DateTimeUtils.convertStringTimeToLong((String) lhs, format) < DateTimeUtils.convertStringTimeToLong((String) rhs, format)) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public static Observable<TreeMap<String, List<Media>>> scanFile(final Context context, String accountName, final MediaType mediaType) {
        return Observable.just(accountName)
                .map(new Func1<String, TreeMap<String,List<Media>>>() {
                    @Override
                    public TreeMap<String, List<Media>> call(String accountName) {
                        if (mediaType == MediaType.HYBIRD) {
                            return  MediaScanner.scan(FileUtils.getRecordThumbsDir(context,accountName), MediaScanner.scan(FileUtils.getSnapshotDir(context,accountName), MediaType.IMAGE), MediaType.RECORD);
                        } else if (mediaType == MediaType.IMAGE) {
                            return  MediaScanner.scan(FileUtils.getSnapshotDir(context, accountName), MediaType.IMAGE);
                        } else {
                            return  MediaScanner.scan(FileUtils.getRecordThumbsDir(context,accountName), MediaType.RECORD);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
