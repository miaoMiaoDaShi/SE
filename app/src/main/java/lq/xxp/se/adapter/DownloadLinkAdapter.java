package lq.xxp.se.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

import lq.xxp.se.Bean.DownloadLink;
import lq.xxp.se.Bean.MagnetLink;
import lq.xxp.se.R;
import lq.xxp.se.utils.DownloadLinkProvider;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadLinkAdapter extends RecyclerView.Adapter<DownloadLinkAdapter.ViewHolder> {

    private List<DownloadLink> links;

    private Activity mParentActivity;

    private DownloadLinkProvider provider;

    public DownloadLinkAdapter(List<DownloadLink> links, Activity mParentActivity, DownloadLinkProvider provider) {
        this.links = links;
        this.mParentActivity = mParentActivity;
        this.provider = provider;
        Log.e("mmds",links.size()+"在下载列表的中的实际数据个数");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_download, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final DownloadLink link = links.get(position);

        holder.parse(link);

        holder.mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!link.hasMagnetLink()) {
                    final ProgressDialog mDialog;
                    mDialog = new ProgressDialog(mParentActivity);
                    mDialog.setTitle("请稍后");
                    mDialog.setMessage("正在获取磁力链接");
                    mDialog.setIndeterminate(false);
                    mDialog.setCancelable(false);
                    mDialog.show();

                    Call<ResponseBody> call = provider.get(link.getLink());
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                MagnetLink magnetLink = provider.parseMagnetLink(response.body().string());
                                onMagnetGet(magnetLink.getMagnetLink());
                            } catch (Throwable e) {
                                onFailure(call, e);
                            }

                            mDialog.dismiss();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                } else {
                    onMagnetGet(link.getMagnetLink());
                }
            }
        });
    }

    public void onMagnetGet(String magnetLink) {
        if (!magnetLink.isEmpty()) {
            ClipboardManager clip = (ClipboardManager) mParentActivity.getSystemService(Context.CLIPBOARD_SERVICE);
            clip.setPrimaryClip(ClipData.newPlainText("magnet-link", magnetLink));
            Toast.makeText(mParentActivity, "磁力链接：" + magnetLink + " 已复制到剪贴板", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mParentActivity, "磁力链接获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return links == null ? 0 : links.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @ViewInject(R.id.download_title)
        public TextView mTextTitle;

        @ViewInject(R.id.download_size)
        public TextView mTextSize;

        @ViewInject(R.id.download_date)
        public TextView mTextDate;

        @ViewInject(R.id.card_download)
        public CardView mCard;

        public void parse(DownloadLink link) {
            Log.e("mmds",link.getTitle()+"parse");
            mTextSize.setText(link.getSize());
            mTextTitle.setText(link.getTitle());
            mTextDate.setText(link.getDate());
        }

        public ViewHolder(View view) {
            super(view);
            ViewUtils.inject(this, view);
        }
    }
}
