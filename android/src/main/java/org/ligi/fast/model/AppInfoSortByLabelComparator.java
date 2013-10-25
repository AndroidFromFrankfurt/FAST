package org.ligi.fast.model;

import java.util.Comparator;

class AppInfoSortByLabelComparator implements Comparator<AppInfo> {

    @Override
    public int compare(AppInfo lhs, AppInfo rhs) {
        return lhs.getLabel().toLowerCase().compareTo(rhs.getLabel().toLowerCase());
    }

}
