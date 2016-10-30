package in.ganitlabs.rnator;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import in.ganitlabs.rnator.Helpers.DataBaseHelper;

public class FragmentUpdates extends Fragment implements Config {

    private static HashMap<Long, String> downloadObj= new HashMap<>();
    private static DownloadManager dm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final MainActivity activityInstance = (MainActivity) getActivity();
        activityInstance.ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        activityInstance.ab.setDisplayHomeAsUpEnabled(true);
        View v;
        if (activityInstance.possibleDownloads == null || activityInstance.possibleDownloads.length() <= 0){
            v = inflater.inflate(R.layout.fragment_updates_empty_layout,container,false);
            ((TextView) v.findViewById(R.id.tvMessage)).setText("Hooray..! Up to date.");
        }else {
            v = inflater.inflate(R.layout.fragment_updates_layout, container, false);

            // TODO pull to refresh and happyuptodate view for no updates
            // Keep these comments here till the implementation of pull to refresh on recyclerView

//        ImageButton btnRefresh = ((ImageButton) v.findViewById(R.id.btnRefreshUpdates));
//        btnRefresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                VolleySingleton.getInstance(getContext()).addToRequestQueue(activityInstance.jsonArrRequest);
//                // refresh recyclerView
//            }
//        });

            RecyclerView rvUpdates = (RecyclerView) v.findViewById(R.id.rvUpdates);
            final UpdatesAdapter adapter = new UpdatesAdapter(getContext(), activityInstance.possibleDownloads);
            rvUpdates.setAdapter(adapter);
            rvUpdates.setLayoutManager(new LinearLayoutManager(getContext()));

            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                        long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                        if (downloadObj.containsKey(downloadId)) {
                            DownloadManager.Query query = new DownloadManager.Query();
                            query.setFilterById(downloadId);
                            Cursor c = dm.query(query);
                            if (c.moveToFirst()) {
                                int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                                if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                                    try {
                                        String fileName = downloadObj.get(downloadId);
                                        FileInputStream fis = new FileInputStream(dm.openDownloadedFile(downloadId).getFileDescriptor());
                                        if (fileName.endsWith(".db")) {
                                            DataBaseHelper.getInstance().installDB(fis);
                                        } else if (fileName.endsWith(".html") && activityInstance.helperDirectory.containsKey(fileName)) {
                                            activityInstance.helperDirectory.get(fileName).installFile(fis);
                                        }

                                        adapter.notifyItemRemoved(adapter.removeFile(fileName));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            };
            getContext().registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            dm = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        }
        return v;
    }

    public static class UpdatesAdapter extends RecyclerView.Adapter<UpdatesAdapter.ViewHolder> {

        static class ViewHolder extends RecyclerView.ViewHolder {
            CardView cardView;
            TextView textView;
            Button btnUpdateAction;
            ViewHolder(View itemView) {
                super(itemView);
                cardView = (CardView) itemView.findViewById(R.id.updateCard);
                textView = (TextView) itemView.findViewById(R.id.tvUpdate);
                btnUpdateAction = (Button) itemView.findViewById(R.id.btnUpdateAction);
            }
        }

        private Context mContext;
        private JSONArray mupdates;

        UpdatesAdapter(Context context, JSONArray updates) {
            mContext = context;
            mupdates = updates;
        }

        int removeFile(String fileName){
            try {
                int i = 0;
                for (; i < mupdates.length() && !((JSONObject)mupdates.get(i)).get("name").equals(fileName);i++){}
                mupdates.remove(i);
                return i;
            } catch (JSONException e) {
                e.printStackTrace();
                return -1;
            }
        }

        @Override
        public UpdatesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View updateView = inflater.inflate(R.layout.update_row_layout, parent, false);

            ViewHolder viewHolder = new ViewHolder(updateView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(UpdatesAdapter.ViewHolder viewHolder, int position) {
            JSONObject update = null;
            try {
                update = mupdates.getJSONObject(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            TextView textView = viewHolder.textView;
            Button btnAction = viewHolder.btnUpdateAction;
            try {
                final String fileName;
                if (update != null) {
                    fileName = update.getString("name");
                    String tvText = fileName + "_v" + update.getInt("version");
                    textView.setText(tvText);
                    btnAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(HOST + Downloads_API + fileName));
                            request.setTitle("Downloading " + fileName);
                            downloadObj.put(dm.enqueue(request), fileName);
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (mupdates!=null) {
                return mupdates.length();
            }else {
                return 0;
            }
        }
    }
}