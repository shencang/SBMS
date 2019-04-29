package com.shencangblue.jin.sbms.Activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.shencangblue.jin.sbms.Model.Book;
import com.shencangblue.jin.sbms.R;
import com.shencangblue.jin.sbms.Utils.SqlDeal;

import org.litepal.LitePal;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button addBtn, queryBtn;
    private ListView reslutLV;
    private LinearLayout titleLL;
    private List<Book> allBook;
    private Book newBook;
    private Book otherBook;
    private PopupWindow pw;
    private TextView deleteTv, careTv, modifyTv;
    private MyAdapter adapter;
    private SqlDeal SQLdeal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLdeal = new SqlDeal();
        LitePal.initialize(this);
        initView();
        initData();
    }

    private void initView() {
        addBtn = (Button) findViewById(R.id.addBtn);
        queryBtn = (Button) findViewById(R.id.queryBtn);
        reslutLV = (ListView) findViewById(R.id.resultLV);
        titleLL = (LinearLayout) findViewById(R.id.titleLL);

    }


    private void initData() {
        allBook = SQLdeal.findAll();
        adapter = new MyAdapter();
        reslutLV.setAdapter(adapter);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCustomizeDialog();
            }
        });


        /**
         * listview的条目点击事件
         */
        reslutLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private String na;

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                //这里留意 getId()不一定对;
                na = String.valueOf(allBook.get(position).getId());
                View v = View.inflate(MainActivity.this, R.layout.adapter_popu_window, null);
                if (pw != null) {
                    pw.dismiss();//让弹出的PopupWindow消失
                    pw = null;
                }
                pw = new PopupWindow(v, -2, -2);
                int[] location = new int[2];
                view.getLocationInWindow(location);
                pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                pw.showAtLocation(parent, Gravity.RIGHT + Gravity.TOP, 20, location[1] - 5);//设置显示的位置
                ScaleAnimation animation = new ScaleAnimation(0.3f, 1f, 0.3f, 1f, Animation.RELATIVE_TO_SELF,
                        Animation.RELATIVE_TO_SELF);//弹出的动画
                animation.setDuration(400);//设置动画时间
                v.startAnimation(animation);//开启动画
                deleteTv = (TextView) v.findViewById(R.id.tv_delete);
                careTv = (TextView) v.findViewById(R.id.tv_care);
                modifyTv = (TextView) v.findViewById(R.id.tv_modify);
                /**
                 * 删除每一个item上的数据
                 */
                deleteTv.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        SQLdeal.delete(Integer.parseInt(na));
                        allBook.remove(position);//移除item的条目
                        allBook = SQLdeal.findAll();//调用查询所有重新再查找一遍
                        adapter.notifyDataSetChanged();//更新适配器
                    }
                });
                /**
                 * 展开一个数据详情功能
                 */
                careTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Book exBook = SQLdeal.findBookId(Integer.parseInt(na));
                        careCustomizeDialog(exBook);

                    }
                });
                /**
                 * 修改一个数据块功能
                 */
                modifyTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Book exBook = SQLdeal.findBookId(Integer.parseInt(na));
                        modifyCustomizeDialog(na,exBook);

                    }
                });

            }
        });

        /**
         * listview的滑动监听
         * 当鼠标上下滑动的时候让PopupWindow消失
         */
        reslutLV.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (pw != null) {
                    pw.dismiss();
                    pw = null;
                }
            }
        });
        /**
         * 查询按钮监听
         */
        queryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();

            }
        });
    }

    /**
     * 查询功能对话框
     */
    private void showInputDialog() {
        final String[] dResult = new String[1];
        /*@setView 装入一个EditView
         */
        final EditText editText = new EditText(MainActivity.this);
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(MainActivity.this);
        inputDialog.setTitle("请输入要查询的主题").setView(editText);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this,
                                editText.getText().toString(),
                                Toast.LENGTH_SHORT).show();
                        dResult[0] = editText.getText().toString().trim();

                        allBook.clear();
                        //Log.e("found",newNotes.getId()+" "+newNotes.getTheme()+" "+newNotes.getContent()+" "+newNotes.getDate());
                        allBook.add(SQLdeal.findBookName(dResult[0]));//调用查询所有重新再查找一遍
                        adapter.notifyDataSetChanged();//更新适配器

                    }
                }).show();
    }


    /**
     * 自定义弹出框———添加详情页
     */
    private void addCustomizeDialog() {
        /* @setView 装入自定义View ==> R.layout.dialog_customize
         * 由于dialog_customize.xml只放置了一个EditView，因此和图8一样
         * dialog_customize.xml可自定义更复杂的View
         */
        AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(MainActivity.this);
        final View dialogView = LayoutInflater.from(MainActivity.this)
                .inflate(R.layout.add_dialog,null);
        TextView idTV = (TextView)dialogView.findViewById(R.id.showIdTv_mod);
        final EditText authorETMod =
                (EditText) dialogView.findViewById(R.id.modAuthorEt);
        final EditText bookNameETMod =
                (EditText) dialogView.findViewById(R.id.modBookNameEt);
        final EditText pagesETMod =
                (EditText) dialogView.findViewById(R.id.modPagesEt);
        final EditText  priceETMod =
                (EditText) dialogView.findViewById(R.id.modPriceEt);

        customizeDialog.setView(dialogView);
        customizeDialog.setPositiveButton("确定添加",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 获取EditView中的输入内容
                        int pagesInt;
                        double priceDou;
                        if (pagesETMod.getText().toString().equals("")||priceETMod.getText().toString().equals("")){
                            pagesInt = -1;
                            priceDou = -1.0;
                        }else {
                            pagesInt = Integer.parseInt(pagesETMod.getText().toString().trim());
                            priceDou = Double.parseDouble(priceETMod.getText().toString().trim());
                        }
                        String authorStr = authorETMod.getText().toString().trim();
                        String bookNametStr = bookNameETMod.getText().toString().trim();

                        //newBook = new Note(themeStr, contentStr, dateStr);
                        if (TextUtils.isEmpty(authorStr)
                                || TextUtils.isEmpty(bookNametStr)
                                || pagesInt==-1
                                || priceDou==-1.0
                                ) {
                            Toast.makeText(MainActivity.this, "添加信息不能为空", Toast.LENGTH_LONG).show();

                        } else {

                            if (SQLdeal.boofindBookName(bookNametStr)) {
                                Toast.makeText(MainActivity.this, "添加的主题名不能一样！", Toast.LENGTH_SHORT).show();

                            } else {
                                boolean add = SQLdeal.addData(authorStr,bookNametStr,pagesInt,priceDou);
                                if (add) {
                                    allBook = SQLdeal.findAll();
                                    adapter.notifyDataSetInvalidated();
                                    Toast.makeText(MainActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
        customizeDialog.show();
    }
    /**
     * 自定义弹出框———数据详情页
     */
    private void careCustomizeDialog(final Book disBook) {
        /* @setView 装入自定义View ==> R.layout.dialog_customize
         * 由于dialog_customize.xml只放置了一个EditView，因此和图8一样
         * dialog_customize.xml可自定义更复杂的View
         */
        AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(MainActivity.this);
        final View dialogView = LayoutInflater.from(MainActivity.this)
                .inflate(R.layout.care_dialog,null);
        TextView idTv =
                (TextView) dialogView.findViewById(R.id.showIdTv);
        TextView authorTv =
                (TextView) dialogView.findViewById(R.id.showAuthorTv);
        TextView bookNameTv =
                (TextView) dialogView.findViewById(R.id.showBookNameTv);
        TextView pagesTv =
                (TextView) dialogView.findViewById(R.id.showPagesTv);
        TextView priceTv =
                (TextView) dialogView.findViewById(R.id.showPriceTv);
        idTv.setText(String.valueOf(disBook.getId()));
        authorTv.setText(disBook.getAuthor());
        bookNameTv.setText(disBook.getName());
        pagesTv.setText(String.valueOf(disBook.getPages()));
        priceTv.setText(String.valueOf(disBook.getPrice()));
        customizeDialog.setView(dialogView);
        customizeDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
        customizeDialog.show();
    }

    /**
     * 自定义弹出框———数据修改页
     */
    private void modifyCustomizeDialog(final String sid, Book exBook) {
        /* @setView 装入自定义View ==> R.layout.dialog_customize
         * 由于dialog_customize.xml只放置了一个EditView，因此和图8一样
         * dialog_customize.xml可自定义更复杂的View
         */
        AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(MainActivity.this);
        final View dialogView = LayoutInflater.from(MainActivity.this)
                .inflate(R.layout.modify_dialog,null);
        TextView idTV = (TextView)dialogView.findViewById(R.id.showIdTv_mod);
        final EditText authorETMod =
                (EditText) dialogView.findViewById(R.id.modAuthorEt);
        final EditText bookNameETMod =
                (EditText) dialogView.findViewById(R.id.modBookNameEt);
        final EditText pagesETMod =
                (EditText) dialogView.findViewById(R.id.modPagesEt);
        final EditText  priceETMod =
                (EditText) dialogView.findViewById(R.id.modPriceEt);
        idTV.setText(String.valueOf(exBook.getId()));
        SpannableString authorSS = new SpannableString(exBook.getAuthor());
        SpannableString bookNameSS = new SpannableString(exBook.getName());
        SpannableString pagesSS = new SpannableString(String.valueOf(exBook.getPrice()));
        SpannableString priceSS = new SpannableString(String.valueOf(exBook.getPrice()));
        authorETMod.setHint(authorSS);
        bookNameETMod.setHint(bookNameSS);
        pagesETMod.setHint(pagesSS);
        priceETMod.setHint(priceSS);
        customizeDialog.setView(dialogView);
        customizeDialog.setPositiveButton("确定修改",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 获取EditView中的输入内容
                        int pagesInt;
                        double priceDou;
                        if (pagesETMod.getText().toString().equals("")||priceETMod.getText().toString().equals("")){
                            pagesInt = -1;
                            priceDou = -1.0;
                        }else {
                            pagesInt = Integer.parseInt(pagesETMod.getText().toString().trim());
                            priceDou = Double.parseDouble(priceETMod.getText().toString().trim());
                        }
                        String authorStr = authorETMod.getText().toString().trim();
                        String bookNametStr = bookNameETMod.getText().toString().trim();

                        //newBook = new Note(themeStr, contentStr, dateStr);
                        if (TextUtils.isEmpty(authorStr)
                                || TextUtils.isEmpty(bookNametStr)
                                || pagesInt==-1
                                || priceDou==-1.0
                        ) {
                            Toast.makeText(MainActivity.this, "修改信息不能为空", Toast.LENGTH_LONG).show();

                        } else {
                            //  otherBook = new Note(bookNametStr);
                            // Book findTheme = server.findName(otherNote);
                            if (SQLdeal.boofindBookName(bookNametStr)) {
                                Toast.makeText(MainActivity.this, "修改的书名不能一样！", Toast.LENGTH_SHORT).show();

                            } else {
                                boolean modify = SQLdeal.upDate(Long.parseLong(sid),authorStr,bookNametStr,pagesInt,priceDou);
                                if (modify) {
                                    allBook = SQLdeal.findAll();
                                    adapter.notifyDataSetInvalidated();
                                    Toast.makeText(MainActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
        customizeDialog.show();
    }


    class MyAdapter extends BaseAdapter {
        private int id2;
        private String author2;
        private String bookName2;
        private int pages2;
        private double price2;
        private View view;


        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;//设置静态类使其初始化
            if (convertView == null) {

                holder = new ViewHolder();//创建holder对象
                view = View.inflate(MainActivity.this, R.layout.item, null);

                holder.idTv = (TextView) view.findViewById(R.id.list_idTV);
                holder.authorTv = (TextView) view.findViewById(R.id.list_authorTV);
                holder.bookNameTv = (TextView) view.findViewById(R.id.list_bookNameTV);
                holder.pagesTv = (TextView) view.findViewById(R.id.list_pagesTV);
                holder.priceTv = (TextView)view.findViewById(R.id.list_priceTV);
                view.setTag(holder);//用来保存一些数据结构。
            } else {
                view = convertView;//复用历史缓存
                holder = (ViewHolder) view.getTag();

            }
            //这里注意
            id2 = allBook.get(position).getId();
            author2 =allBook.get(position).getAuthor();
            bookName2 =allBook.get(position).getName();
            pages2 = allBook.get(position).getPages();
            price2 =allBook.get(position).getPrice();

            holder.idTv.setText(String.valueOf(id2));
            holder.authorTv.setText(author2);
            holder.bookNameTv.setText(bookName2);
            holder.pagesTv.setText(String.valueOf(pages2));
            holder.priceTv.setText(String.valueOf(price2));
            return view;
        }

        @Override
        public int getCount() {
            return allBook.size();    //返回list集合中的数据个数
        }

        @Override
        public Object getItem(int position) {

            return null;
        }

        @Override
        public long getItemId(int position) {

            return 0;
        }


    }

    //ViewHolder静态类
    static class ViewHolder {
        TextView idTv;
        TextView authorTv;
        TextView bookNameTv;
        TextView pagesTv;
        TextView priceTv;
    }

}


