package bkoumtak.udacity.moviebrowser.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by kondeelai on 2017-01-12.
 */

@Database(version = FavoritesDatabase.VERSION)
public class FavoritesDatabase {
    private FavoritesDatabase(){}

    public static final int VERSION = 2;

    @Table(FavoritesColumns.class) public static final String FAVORITES = "favorites";
}
