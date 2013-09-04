package org.ligi.fast;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

public class AppInfoList {

    private static List<AppInfo> pkgAppsListShowing;
    private static List<AppInfo> pkgAppsListAll;
    private String query = "";

    public enum SortMode {
        UNSORTED, ALPHABETICAL
    }

    @SuppressWarnings("unchecked")
    public AppInfoList(List<AppInfo> pkgAppsListAll) {
        this.pkgAppsListAll = new ArrayList<AppInfo>();
        this.pkgAppsListAll.addAll(pkgAppsListAll);

        new IconCacheTask().execute(this.pkgAppsListAll);

        setQuery(query); // to rebuild the showing list
    }

    public void setAppsList(List<AppInfo> pkgAppsListAll) {

    }

    public void setSortMode(SortMode mode) {
        if (mode.equals(SortMode.ALPHABETICAL)) {
            java.util.Collections.sort(pkgAppsListAll, new AppInfoSortByLabelComparator());
        }
    }

    public int getCount() {
        return pkgAppsListShowing.size();
    }

    public AppInfo get(int pos) {
        return pkgAppsListShowing.get(pos);
    }

    private static class IconCacheTask extends AsyncTask<List<AppInfo>, Void, Void> {
        protected Void doInBackground(List<AppInfo>... params) {
            List<AppInfo> all = params[0];
            for (AppInfo info:all) {
                info.getIcon();
            }
            return null;
        }
    }

    public void setQuery(String act_query) {
        // note the alternate query approach is not exact - doesn't match all permutations of replacements, but
        // is FASTer than exact and totally enough for most cases
        String actAlternateQuery;

        if (App.getSettings().isUmlautConvertActivated()) {
            actAlternateQuery = act_query.replaceAll("ue", "ü").replaceAll("oe", "ö").replaceAll("ae", "ä").replaceAll("ss", "ß");
        } else {
            actAlternateQuery = null;
        }

        this.query = act_query;

        ArrayList<AppInfo> pkgAppsListFilter = new ArrayList<AppInfo>();

        for (AppInfo info : pkgAppsListAll) {
            if (appInfoMatchesQuery(info, act_query) || appInfoMatchesQuery(info, actAlternateQuery)) {
                pkgAppsListFilter.add(info);
            }
        }

        pkgAppsListShowing = pkgAppsListFilter;
    }

    public String getQuery() {
        return query;
    }

    private boolean appInfoMatchesQuery(AppInfo info, String query) {
        if (info.getLabel().toLowerCase().contains(query)) {
            return true;
        }

        // also search in package name when activated
        return App.getSettings().isSearchPackageActivated() && (info.getPackageName().toLowerCase().contains(query));

    }

}
