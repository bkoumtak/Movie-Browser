package bkoumtak.udacity.moviebrowser.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by kondeelai on 2017-01-11.
 */
public class FavoritesColumns {

    @DataType (DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    public static final String _ID = "_id";

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String TITLE = "title";
    @DataType(DataType.Type.INTEGER) @NotNull
    public static final String RATING = "rating";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String SYNOPSIS = "synopsis";
}
