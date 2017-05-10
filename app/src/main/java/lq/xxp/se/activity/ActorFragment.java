package lq.xxp.se.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import lq.xxp.se.Bean.ActorBean;
import lq.xxp.se.R;
import lq.xxp.se.adapter.ActorRyAdapter;
import lq.xxp.se.utils.Base64;


public class ActorFragment extends Fragment {
    private ActorFragment mActorFragment;

    @ViewInject(R.id.rv_actor)
    private RecyclerView rv_actor;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.actor_fragment,container,false);
        ViewUtils.inject(this, view);
        initView();
        return view;

    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv_actor.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        layoutManager.setOrientation(OrientationHelper. VERTICAL);

        rv_actor.setItemAnimator( new DefaultItemAnimator());

        new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ActorRyAdapter actorRyAdapter = new ActorRyAdapter(getActivity(),loadJson(getJson()));
                        rv_actor.setAdapter(actorRyAdapter);
                    }
                });

            }
        }).start();

    }


    private List<ActorBean> loadJson(String json) {
        List<ActorBean> actors = new ArrayList<ActorBean>();
        try {
            JSONObject jb = new JSONObject(json);
            JSONArray ja = jb.getJSONArray("actors");
            for(int i=0;i<ja.length();i++){
                jb = ja.getJSONObject(i);
                String decodephotoUrl = new String(Base64.decode(jb.getString("photoUrl")));
                String decodeBlockLink = new String(Base64.decode(jb.getString("blockLink")));
                String decodeName = new String(Base64.decode(jb.getString("name")));
                //String decodeNum = new String(Base64.decode(jb.getString("num")));
                //String decodeTime = new String(Base64.decode(jb.getString("time")));
                actors.add(new ActorBean(decodephotoUrl,decodeBlockLink,decodeName));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return actors;
    }

    private String getJson() {
        String encode = "";
        try {
            InputStreamReader isr = new InputStreamReader(getResources().openRawResource(R.raw.all_actor));
            BufferedReader br = new BufferedReader(isr);
            String readLine = "";
            while ((readLine=br.readLine())!=null){
                encode += readLine;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return encode;
    }
}
