package com.byd.dynaudio_app.utils;

import static com.byd.dynaudio_app.bean.ColumnBean.ColumnBeanType.ITEM_TYPE_3D_IMMERSION_SOUND;
import static com.byd.dynaudio_app.bean.ColumnBean.ColumnBeanType.ITEM_TYPE_AUDIO_COLUMN;
import static com.byd.dynaudio_app.bean.ColumnBean.ColumnBeanType.ITEM_TYPE_BANNER;
import static com.byd.dynaudio_app.bean.ColumnBean.ColumnBeanType.ITEM_TYPE_BLACK_PLASTIC;
import static com.byd.dynaudio_app.bean.ColumnBean.ColumnBeanType.ITEM_TYPE_SELECTION_OF_GOLDEN_SONGS;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.bean.ColumnBean;
import com.byd.dynaudio_app.bean.ItemBean;
import com.byd.dynaudio_app.bean.ModuleInfo;
import com.byd.dynaudio_app.bean.PlayItemBean;
import com.byd.dynaudio_app.bean.TopBgBean;
import com.byd.dynaudio_app.manager.MusicPlayManager;
import com.lzy.widget.manager.ExpandLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个工具类用来制造假数据 用于自测ui
 */
public class TestUtils {
    public static List<ItemBean> getTestBlackPlasticList() {
        ArrayList<ItemBean> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ItemBean bean = new ItemBean();
            bean.setLabel(i % 2 == 0 ? "全景声" : "");
            bean.setTitle("   2023格莱美音乐奖获奖及提名全收录");
            bean.setSubTitle("丹拿x艺术工作室");
            bean.setContent("我是内容...");
            bean.setSubContent("Adele、Taylor Swift、Sam Smith...");
//            bean.setShowImg("http://b247.photo.store.qq.com/psb?/V11ZojBI312o2K/63aY8a4M5quhi.78*krOo7k3Gu3cknuclBJHS3g1fpc!/b/dDXWPZMlBgAA");
            bean.setShowImg(getImagePath(i));
            list.add(bean);
        }

        return list;
    }

    private static String getImagePath(int i) {
        String imgPath = "";
        switch (i % 4) {
            case 0:
                imgPath = "https://img0.baidu.com/it/u=3292276763,3513150805&fm=253&fmt=auto&app=138&f=JPEG?w=600&h=396";
                break;
            case 1:
                imgPath = "https://img2.baidu.com/it/u=1003452936,3029635370&fm=253&fmt=auto&app=138&f=JPEG?w=524&h=500";
                break;
            case 2:
                imgPath = "https://img1.baidu.com/it/u=465282398,1111101338&fm=253&fmt=auto&app=138&f=JPEG?w=834&h=500";
                break;
            case 3:
                imgPath = "https://img2.baidu.com/it/u=2025096697,1719833567&fm=253&fmt=auto&app=138&f=JPEG?w=450&h=599";
                break;
        }
        return imgPath;
    }

    private static List<ItemBean> getTestAudioColumn() {
        ArrayList<ItemBean> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ItemBean bean = new ItemBean();
            bean.setLabel(i % 2 == 0 ? "独家上线" : "丹拿自制");
            bean.setTitle("郎朗巴黎大师课");
            bean.setSubTitle("国家大剧院");
            bean.setContent("课程 | 共60期");
            bean.setSubContent("带你走进古典音乐殿堂，文案过长...");
            bean.setSubContent("Adele、Taylor Swift、Sam Smith...");
            bean.setMusic(i % 2 == 0);
            bean.setLabel(i % 2 == 0 ? "自制" : "独家");
            bean.setShowImg("http://b247.photo.store.qq.com/psb?/V11ZojBI312o2K/63aY8a4M5quhi.78*krOo7k3Gu3cknuclBJHS3g1fpc!/b/dDXWPZMlBgAA");
            bean.setShowImg("https://img1.baidu.com/it/u=3560390732,1339934170&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500");
            list.add(bean);
        }

        return list;
    }

    public static List<ColumnBean> getMainTestList() {
        List<ColumnBean> list = new ArrayList<>();

        ColumnBean columnBean0 = new ColumnBean();
        columnBean0.setType(ITEM_TYPE_BANNER);
        list.add(columnBean0);

        ColumnBean columnBean = new ColumnBean();
        columnBean.setType(ITEM_TYPE_SELECTION_OF_GOLDEN_SONGS);
//        columnBean.setData(new ArrayList<>());
        list.add(columnBean);

        ColumnBean columnBean2 = new ColumnBean();
        columnBean2.setType(ITEM_TYPE_BLACK_PLASTIC);
        columnBean2.setTitle("黑胶专区");
//        columnBean2.setData(getTestBlackPlasticList());
        list.add(columnBean2);

        ColumnBean columnBean3 = new ColumnBean();
        columnBean3.setType(ITEM_TYPE_AUDIO_COLUMN);
        columnBean3.setTitle("有声专栏");
//        columnBean3.setData(getTestAudioColumn());
        list.add(columnBean3);

        ColumnBean columnBean4 = new ColumnBean();
        columnBean4.setType(ITEM_TYPE_3D_IMMERSION_SOUND);
        columnBean4.setTitle("沉浸音专区");
//        columnBean4.setData(getTestBlackPlasticList());
        list.add(columnBean4);

//        ColumnBean columnBean5 = new ColumnBean();
//        columnBean5.setType(ITEM_TYPE_3D_IMMERSION_SOUND);
////        columnBean4.setData(getTestBlackPlasticList());
//        list.add(columnBean5);

//        ColumnBean columnBean5 = new ColumnBean();
//        columnBean5.setType(ITEM_TYPE_3D_IMMERSION_SOUND);
//        columnBean5.setTitle("新专区");
//        columnBean5.setData(getTestBlackPlasticList());
//        list.add(columnBean5);


        return list;
    }

    /**
     * 有一个recycleview 竖着的 对其捏造假数据展示
     *
     * @param mContext 上下文
     * @param recycler recycle
     * @param n        数据量
     */
    public static void setRecyclerFakeAdapter(Context mContext, RecyclerView recycler, int n) {
        String[] strings = new String[n];
        for (int i = 0; i < n; i++) {
            strings[i] = "我是第" + i + "个数据";
        }

        recycler.setLayoutManager(new ExpandLinearLayoutManager(mContext));



        recycler.setAdapter(new ListAdapter(new DiffUtil.ItemCallback() {
            @Override
            public boolean areItemsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
                return false;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
                return false;
            }
        }) {

            @Override
            public int getItemCount() {
                return strings.length;
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_item, parent, false)) {
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                TextView textView = holder.itemView.findViewById(R.id.tv_item);
                textView.setText(strings[position]);
            }
        });
    }

    public static List<ItemBean> getBannerTestList() {
        ArrayList<ItemBean> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ItemBean bean = new ItemBean();
            bean.setTitle("新闻资讯");
            // bean.setShowImg("https://pic.ibaotu.com/02/14/92/22s888piC9GI.jpg-1.jpg%21ww7002");
            bean.setShowImg(getImagePath(i));
            list.add(bean);
        }
        return list;
    }

    public static List<TopBgBean> getTopBg() {
        ArrayList<TopBgBean> list = new ArrayList<>(2);
        list.add(new TopBgBean("丹拿智能音乐座舱", "全方位体验音乐厅级的极致音乐享受", "https://img2.baidu.com/it/u=1785223586,4118298843&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500"));
        list.add(new TopBgBean("专属调音", "定制化音效体验，至臻还原纯净享受", "https://img2.baidu.com/it/u=3083113435,1076574163&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500"));
        return list;
    }

    public static ArrayList<String> getTestLyrics() {
        ArrayList<String> list = new ArrayList<>();
//        list.add("");
        for (int i = 0; i < 30; i++) {
//            list.add("A kiss is still a kiss");
//            list.add("The fundamental things apply");

            list.add(String.valueOf(i).repeat(12));
        }
//        for (int i = 0; i < 6; i++) {
//            list.add("");
//        }
//        list.add(".......");
        return list;
    }

    public static String getTestSingerPic() {
        return "https://img0.baidu.com/it/u=464805808,685289145&fm=253&fmt=auto&app=120&f=JPEG?w=1280&h=800";
    }

    public static List<PlayItemBean> getTestPlayList() {
        ArrayList<PlayItemBean> playItemBeans = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            int po = i;
            PlayItemBean playItemBean = new PlayItemBean();
            playItemBean.setTitle("我是标题: " + (po + 1));
            playItemBean.setSubTitle("我是副标题: " + (po + 1));
            playItemBean.setRecommend(i % 4 == 0 ? "Hi-Res" : "");
            playItemBean.setCollect(i % 3 == 0);
            playItemBean.setImgShow(getImagePath(i));
            playItemBean.setVideoPath(i % 4 == 0 ? getVideoPath() : null);
            playItemBean.setMusicPath(getMusicPath());
            playItemBean.setSinger("Miley Cyrus /马友友");
            playItemBean.setSize("162M");
            playItemBean.setDuration("2’55");

//            switch (i % 3) {
//                case 0:
//                    playItemBean.setType(MusicPlayManager.ItemType.Music);
//                    playItemBean.setLrcPath(getLrcPath());
//                    break;
//                case 1:
//                    playItemBean.setType(MusicPlayManager.ItemType.PureMusic);
//                    break;
//                case 2:
//                    playItemBean.setType(MusicPlayManager.ItemType.LanguageClass);
//                    break;
//            }
            playItemBeans.add(playItemBean);
        }
        return playItemBeans;
    }

    private static String getMusicPath() {
        String musicPath = "";
        return musicPath;
    }

    private static String getLrcPath() {
        String lrcPath = "www.baidu.com";
        return lrcPath;
    }

    private static String getVideoPath() {
        String videoPath = "https://prod-streaming-video-msn-com.akamaized.net/c0f610f9-4fb1-4222-9ab5-a62f630a03ac/8caf2d97-2c57-48c9-8582-808b42089e3f.mp4";
        return videoPath;
    }

    public static String getTestBg() {
//        return "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fsafe-img.xhscdn.com%2Fbw1%2Ff0951e5f-3a74-46ea-a252-ad89841ee34d%3FimageView2%2F2%2Fw%2F1080%2Fformat%2Fjpg&refer=http%3A%2F%2Fsafe-img.xhscdn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1680926043&t=a98e572bfe71ca5c62dba478d4b77dad";
        return "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fsafe-img.xhscdn.com%2Fbw1%2F7eb3ab0a-d531-4b83-94eb-0369bb535807%3FimageView2%2F2%2Fw%2F1080%2Fformat%2Fjpg&refer=http%3A%2F%2Fsafe-img.xhscdn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1680930832&t=87f0ca0f372d58aab105137a85ff4d96";
    }

    public static ModuleInfo getTestModuleInfo(int i) {
        ModuleInfo moduleInfo = new ModuleInfo();
        switch (i) {
            case 0:  // 金曲
                moduleInfo.setModuleName("甄选金曲");
                moduleInfo.setTitle("HIFI MUSIC");

                break;
        }
        return null;
    }
}
