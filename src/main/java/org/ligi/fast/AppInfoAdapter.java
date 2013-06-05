package org.ligi.fast;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Adapter to provide the AppInfo to the user - also care for the filtering of the query
 *
 * @author Marcus -ligi- Büschleb
 *         jfreax
 *         <p/>
 *         License GPLv3
 */
public class AppInfoAdapter extends BaseAdapter {

    private static List<AppInfo> pkgAppsListShowing;
    private static List<AppInfo> pkgAppsListAll;
    private Context ctx;
    private String act_query = "";
    private String colorString = "";
    private SortMode sort_mode = SortMode.UNSORTED;

    public enum SortMode {
        UNSORTED, ALPHABETICAL, MOST_USED
    }

    public AppInfoAdapter(Context _ctx, List<AppInfo> _pkgAppsListAll) {
        ctx = _ctx;
        setAllAppsList(_pkgAppsListAll);
    }

    @SuppressWarnings("unchecked")
    public void setAllAppsList(List<AppInfo> _pkgAppsListAll) {
        pkgAppsListAll = new ArrayList<AppInfo>();
        pkgAppsListAll.addAll(_pkgAppsListAll);

        new IconCacheTask().execute(pkgAppsListAll);

        setActQuery(act_query); // to rebuild the showing list

        int color = (ctx.getResources().getColor(R.color.divider_color));
        colorString = Integer.toHexString(color).toUpperCase().substring(2);
    }

    public void setSortMode(SortMode mode) {
        sort_mode = mode;
        if (sort_mode.equals(SortMode.ALPHABETICAL))
            java.util.Collections.sort(pkgAppsListAll, new AlphabeticalComparator());
        else if (sort_mode.equals(SortMode.MOST_USED))
            java.util.Collections.sort(pkgAppsListAll, new MostUsedComparator(ctx));
    }

    public int getCount() {
        return pkgAppsListShowing.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) { // || ((ViewHolder)convertView.getTag()).isTextOnlyActive == getPrefs().isTextOnlyActive()) { // if it's not recycled, initialize some

            LayoutInflater mLayoutInflater = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (getPrefs().isTextOnlyActive())
                convertView = mLayoutInflater.inflate(R.layout.item_textonly, null);
            else {
                String size = (new FASTPrefs(ctx).getIconSize());

                if (size.equals("small"))
                    convertView = mLayoutInflater.inflate(R.layout.item_small, null);
                else if (size.equals("large"))
                    convertView = mLayoutInflater.inflate(R.layout.item_large, null);
                else
                    convertView = mLayoutInflater.inflate(R.layout.item, null);

            }
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.textView);
            holder.image = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();
        ImageView imageView = holder.image;
        TextView labelView = holder.text;
        if (imageView != null) {
            Drawable drawable = pkgAppsListShowing.get(position).getIcon();
            holder.image.setImageDrawable(drawable);
        }

        labelView.setMaxLines(getPrefs().getMaxLines());

        String label = pkgAppsListShowing.get(position).getLabel();
        String hightlight_label = label;

        int query_index = label.toLowerCase().indexOf(act_query);

        if (act_query.length() == 0) {
            labelView.setText(label);
            return convertView;
        }

        if (query_index == -1) { // search not App-Name - hope it is in Package Name - why else we want to show the app?
            label = pkgAppsListShowing.get(position).getPackageName();
            label = label.replace("com.google.android.apps.", "");
            query_index = label.toLowerCase().indexOf(act_query);
        }

        if (query_index != -1) {
            hightlight_label = label.substring(0, query_index)
                    + "<font color='#"
                    + colorString
                    + "'>"
                    + label.substring(query_index,
                    query_index + act_query.length())
                    + "</font>"
                    + label.substring(query_index + act_query.length(),
                    label.length());
        }

        labelView.setText(Html.fromHtml(hightlight_label));
        return convertView;
    }


    private boolean appInfoMatchesQuery(AppInfo info, String query) {
        if (info.getLabel().toLowerCase().contains(act_query)) {
            return true;
        }

        // also search in package name when activated
        if (getPrefs().isSearchPackageActivated() && (info.getPackageName().toLowerCase().contains(act_query))) {
            return true;
        }

        return false; // no match till here - we must be false
    }

    public void setActQuery(String act_query) {
        // note the alternate query approach is not exact - doesn't match all permutations of replacements, but
        // is FASTer than exact and totally enough for most cases
        String actAlternateQuery;

        if (getPrefs().isUmlautConvertActivated()) {
            actAlternateQuery = act_query.replaceAll("ue", "ü").replaceAll("oe", "ö").replaceAll("ae", "ä").replaceAll("ss", "ß");
        } else {
            actAlternateQuery = null;
        }

        this.act_query = act_query;

        ArrayList<AppInfo> pkgAppsListFilter = new ArrayList<AppInfo>();

        for (AppInfo info : pkgAppsListAll) {
            if (appInfoMatchesQuery(info, act_query) || appInfoMatchesQuery(info, actAlternateQuery)) {
                pkgAppsListFilter.add(info);
            }
        }

        pkgAppsListShowing = pkgAppsListFilter;
        notifyDataSetChanged();
    }

    public AppInfo getAtPosition(int pos) {
        return pkgAppsListShowing.get(pos);
    }

    public FASTPrefs getPrefs() {
        return ((ApplicationContext) ctx.getApplicationContext()).getPrefs();
    }

    private static class ViewHolder {
        public TextView text;
        public ImageView image;
    }

    private static class IconCacheTask extends AsyncTask<List<AppInfo>, Void, Void> {
        protected Void doInBackground(List<AppInfo>... params) {
            List<AppInfo> all = params[0];
            for (int i = 0; i < all.size(); i++) {
                all.get(i).getIcon();
            }
            return null;
        }
    }

    class AlphabeticalComparator implements Comparator<AppInfo> {

        @Override
        public int compare(AppInfo lhs, AppInfo rhs) {
            return lhs.getLabel().toLowerCase().compareTo(rhs.getLabel().toLowerCase());
        }

        @Override
        public boolean equals(Object object) {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    class MostUsedComparator implements Comparator<AppInfo> {

        private final AlphabeticalComparator alpha = new AlphabeticalComparator();
        private final LaunchHistory history;

        public MostUsedComparator(Context context) {
            history = LaunchHistory.getInstance(context);
        }

        @Override
        public int compare(AppInfo lhs, AppInfo rhs) {
            int lhsCount = history.getCount(lhs);
            int rhsCount = history.getCount(rhs);

            int result = 0;

            if (lhsCount == rhsCount) {
                result = alpha.compare(lhs, rhs);
            } else if (lhsCount < rhsCount) {
                result = 1;
            } else if (lhsCount > rhsCount) {
                result = -1;
            }

            return result;
        }

        @Override
        public boolean equals(Object object) {
            return false;
        }
    }

}