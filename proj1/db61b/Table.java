// This is a SUGGESTED skeleton for a class that represents a single
// Table.  You can throw this away if you want, but it is a good
// idea to try to understand it first.  Our solution changes or adds
// about 100 lines in this skeleton.

// Comments that start with "//" are intended to be removed from your
// solutions.
package db61b;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static db61b.Utils.*;

/** A single table in a database.
 *  @author P. N. Hilfinger
 */
class Table {
    /** A new Table whose columns are given by COLUMNTITLES, which may
     *  not contain duplicate names. */
    Table(String[] columnTitles) {
        if (columnTitles.length == 0) {
            throw error("table must have at least one column");
        }
        _size = 0;
        _rowSize = columnTitles.length;

        for (int i = columnTitles.length - 1; i >= 1; i -= 1) {
            for (int j = i - 1; j >= 0; j -= 1) {
                if (columnTitles[i].equals(columnTitles[j])) {
                    throw error("duplicate column name: %s",
                                columnTitles[i]);
                }
            }
        }
        _titles = columnTitles;
        _columns = new ValueList[_rowSize];
        for (int c = 0; c < _rowSize; c++) {
            _columns[c] = new ValueList();
        }
    }

    /** A new Table whose columns are give by COLUMNTITLES. */
    Table(List<String> columnTitles) {
        this(columnTitles.toArray(new String[columnTitles.size()]));
    }

    /** Return the number of columns in this table. */
    public int columns() {
        return _rowSize;
    }

    /** Return the title of the Kth column.  Requires 0 <= K < columns(). */
    public String getTitle(int k) {
        return _titles[k];
    }

    /** Return the number of the column whose title is TITLE, or -1 if
     *  there isn't one. */
    public int findColumn(String title) {
        for (int i = 0; i < columns(); i++) {
            if (_titles[i].equals(title)) {
                return i;
            }
        }
        return -1;
    }

    /** Return the number of rows in this table. */
    public int size() {
        return _size;
    }

    /** Return the value of column number COL (0 <= COL < columns())
     *  of record number ROW (0 <= ROW < size()). */
    public String get(int row, int col) {
        try {
            return _columns[col].get(row);
        } catch (IndexOutOfBoundsException excp) {
            throw error("invalid row or column");
        }
    }

    /** Add a new row whose column values are VALUES to me if no equal
     *  row already exists.  Return true if anything was added,
     *  false otherwise. */
    public boolean add(String[] values) {
        /* if table is empty */
        if (_columns[0].size() == 0) {
            addUniqueRow(values);
            positionNewRow(size() -1);
            return true;
        }
        /* have at least 1 row */
        for (int r = 0; r < size(); r++) {
            if (identicalRow(values, r)) {
                return false;
            }
        }
        addUniqueRow(values);
        positionNewRow(size() -1);
        //determine the position of the row in the ArrayList
        return true;
    }
    /** Compares a string[] VALUES against _column[R][k] for k between 0 and rowSize */
    public boolean identicalRow(String[] values, int r) {
        for (int c = 0; c < _columns.length; c++) {
            if (!get(r, c).equals(values[c])) {
                return false;
            }
        }
        return true;
    }
    /** Adds a new unique row of values */
    public void addUniqueRow(String[] values) {
        for (int c = 0; c < _columns.length; c++) {
            _columns[c].add(values[c]);
        }
        _size += 1;
    }

    /** repositions new row in the index ArrayList to keep the rows sorted. */
    public void positionNewRow(int r) {
        if (_index.size() == 0) {
            _index.add(r);
            return;
        }
        for (int i = 0; i < _index.size(); i++) {
            if (compareRows(r,_index.get(i)) < 0) {
                _index.add(i, r);
                return;
            }
        }
        _index.add(r);
    }

    /** Add a new row whose column values are extracted by COLUMNS from
     *  the rows indexed by ROWS, if no equal row already exists.
     *  Return true if anything was added, false otherwise. See
     *  Column.getFrom(Integer...) for a description of how Columns
     *  extract values. */
    public boolean add(List<Column> columns, Integer... rows) {
        String[] values = new String[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            values[i] = columns.get(i).getFrom(rows);
        }
        return add(values);
    }

    /** Read the contents of the file NAME.db, and return as a Table.
     *  Format errors in the .db file cause a DBException. */
    static Table readTable(String name) {
        BufferedReader input;
        Table table;
        input = null;
        table = null;
        try {
            input = new BufferedReader(new FileReader(name + ".db"));
            String header = input.readLine();
            String nextLine;
            String[] nextRow;
            if (header == null) {
                throw error("missing header in DB file");
            }
            String[] columnNames = header.split(",");
            // FILL IN
            table = new Table(columnNames);
            while ((nextLine = input.readLine()) != null) {
                nextRow = nextLine.trim().split(",");
                table.add(nextRow);
            }

        } catch (FileNotFoundException e) {
            throw error("could not find %s.db", name);
        } catch (IOException e) {
            throw error("problem reading from %s.db", name);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    /* Ignore IOException */
                }
            }
        }
        return table;
    }

    /** Write the contents of TABLE into the file NAME.db. Any I/O errors
     *  cause a DBException. */
    void writeTable(String name) {
        PrintStream output;
        output = null;
        try {
            String sep;
            sep = "";
            output = new PrintStream(name + ".db");
            // FILL THIS IN
            for (int i = 0; i < _titles.length; i++) {
                if (i == 0) {
                    sep += _titles[i];
                } else {
                    sep = sep + ","+ _titles[i];
                }
            }
            output.println(sep);
            sep = "";
            for (int r = 0; r < _columns[0].size(); r++) {
                for (int c = 0; c < size(); c++) {
                    if (c == 0) {
                        sep += get(r, c);
                    } else {
                        sep = sep + "," + get(r,c);
                    }
                }
                output.println(sep);
                sep = "";
            }
        } catch (IOException e) {
            throw error("trouble writing to %s.db", name);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /** Print my contents on the standard output, separated by spaces
     *  and indented by two spaces. */
    void print() {
        if (_size == 0) {
            return;
        }
        String indent = "  ";
        String start = "";
        for (int r = 0; r < _size; r++) {
            String beg = indent;
            for (int c = 0; c < _columns.length; c++) {
                beg = beg + _columns[c].get(_index.get(r)) + " ";
            }
            System.out.println(beg);
        }
    }


    /** Return a new Table whose columns are COLUMNNAMES, selected from
     *  rows of this table that satisfy CONDITIONS. */
    Table select(List<String> columnNames, List<Condition> conditions) {
        Table result = new Table(columnNames);
        List<Column> selectedColumns = createColumnsFromNames(columnNames, this);
        if (conditions.size() == 0) {
            for (int r = 0; r < size(); r++) {
                result.add(selectedColumns, r);
            }
        } else {
            //case 2: single table with conditions
            for (int r = 0; r < size(); r++) {
                if (shouldAddRow(conditions, r)) {
                    result.add(selectedColumns, r);
                }
            }
        }
        return result;
    }

    /** creates and return a list of column objects from tables. **/
    List<Column> createColumnsFromNames(List<String> columnNames, Table ... tables) {
        ArrayList<Column> columnObjects = new ArrayList<>();
        for (int i = 0; i < columnNames.size(); i++) {
            columnObjects.add(new Column(columnNames.get(i), tables));
        }
        return columnObjects;
    }

    /** Returns if a given columns satisfies all of the conditions */
    boolean shouldAddRow(List<Condition> conditions, int row) {
        for (Condition cond: conditions) {
            if (!cond.test(row)) {
                return false;
            }
        }
        return true;
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected
     *  from pairs of rows from this table and from TABLE2 that match
     *  on all columns with identical names and satisfy CONDITIONS. */
    Table select(Table table2, List<String> columnNames,
                 List<Condition> conditions) {
        Table result = new Table(columnNames);
        List<String> commonColumns = this.getCommonColumns(table2);
        //create columns from common names
        List<Column> common1 = createColumnsFromNames(commonColumns, this);
        List<Column> common2 = createColumnsFromNames(commonColumns, table2);
        //create columns from selected Column values
        List<Column> selectedColumns = createColumnsFromNames(columnNames, this, table2);
        for (int r1 = 0; r1 < this.size(); r1 += 1) {
            for (int r2 = 0; r2 < table2.size(); r2 += 1) {
                //If there are common column names at all
                if (commonColumns.size() > 0) {
                    if (equijoin(common1, common2, r1, r2)) {
                        if (conditions.size() == 0) {
                            result.add(selectedColumns, r1, r2);
                        } else if (Condition.test(conditions, r1, r2)) {
                            result.add(selectedColumns, r1, r2);
                        }
                    }
                } else {
                    if (conditions.size() == 0) {
                        result.add(selectedColumns, r1, r2);
                    } else if (Condition.test(conditions, r1, r2)) {
                        result.add(selectedColumns, r1, r2);
                    }
                }
            }
        }
        return result;
    }

    /** Adds a row from selected Columns applying CONDITIONS WHERE necessary, otherwise simply
     *  add the row. This is a helper method written to condense the code inside of two table
     *  select. It checks first if there are any conditions, if not, row is automatically added.
     */
    static public void addRoWTwoTableSelect(Table table, List<Column> selectedColumns) {
    }

    /** Creates a helper function that returns common columnNames between two tables **/
    public List<String> getCommonColumns(Table table2) {
        ArrayList<String> commonColumns = new ArrayList<>();
        for (String columnName: _titles) {
            if (table2.findColumn(columnName) != -1) {
                commonColumns.add(columnName);
            }
        }
        return commonColumns;
    }

    /** Return <0, 0, or >0 depending on whether the row formed from
     *  the elements _columns[0].get(K0), _columns[1].get(K0), ...
     *  is less than, equal to, or greater than that formed from elememts
     *  _columns[0].get(K1), _columns[1].get(K1), ....  This method ignores
     *  the _index. */
    private int compareRows(int k0, int k1) {
        for (int i = 0; i < _columns.length; i += 1) {
            int c = _columns[i].get(k0).compareTo(_columns[i].get(k1));
            if (c != 0) {
                return c;
            }
        }
        return 0;
    }

    /** Return true if the columns COMMON1 from ROW1 and COMMON2 from
     *  ROW2 all have identical values.  Assumes that COMMON1 and
     *  COMMON2 have the same number of elements and the same names,
     *  that the columns in COMMON1 apply to this table, those in
     *  COMMON2 to another, and that ROW1 and ROW2 are indices, respectively,
     *  into those tables. */
    private static boolean equijoin(List<Column> common1, List<Column> common2,
                                    int row1, int row2) {
        Column leftCol;
        Column rightCol;
        for (int i = 0; i < common1.size(); i++) {
            leftCol = common1.get(i);
            rightCol = common2.get(i);
            if (!leftCol.getFrom(row1).equals(rightCol.getFrom(row2))) {
                return false;
            }
        }
        return true;
    }

    /** A class that is essentially ArrayList<String>.  For technical reasons,
     *  we need to encapsulate ArrayList<String> like this because the
     *  underlying design of Java does not properly distinguish between
     *  different kinds of ArrayList at runtime (e.g., if you have a
     *  variable of type Object that was created from an ArrayList, there is
     *  no way to determine in general whether it is an ArrayList<String>,
     *  ArrayList<Integer>, or ArrayList<Object>).  This leads to annoying
     *  compiler warnings.  The trick of defining a new type avoids this
     *  issue. */
    private static class ValueList extends ArrayList<String> {
    }

    /** My column titles. */
    private final String[] _titles;
    /** My columns. Row i consists of _columns[k].get(i) for all k. */
    private final ValueList[] _columns;

    /** Rows in the database are supposed to be sorted. To do so, we
     *  have a list whose kth element is the index in each column
     *  of the value of that column for the kth row in lexicographic order.
     *  That is, the first row (smallest in lexicographic order)
     *  is at position _index.get(0) in _columns[0], _columns[1], ...
     *  and the kth row in lexicographic order in at position _index.get(k).
     *  When a new row is inserted, insert its index at the appropriate
     *  place in this list.
     *  (Alternatively, we could simply keep each column in the proper order
     *  so that we would not need _index.  But that would mean that inserting
     *  a new row would require rearranging _rowSize lists (each list in
     *  _columns) rather than just one. */
    private final ArrayList<Integer> _index = new ArrayList<>();

    /** My number of rows (redundant, but convenient). */
    private int _size;
    /** My number of columns (redundant, but convenient). */
    private final int _rowSize;
}
