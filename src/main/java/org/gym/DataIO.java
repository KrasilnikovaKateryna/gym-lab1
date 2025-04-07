package org.gym;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public interface DataIO<T> {
    void exportData(List<T> data, File file, Comparator<T> sorter) throws IOException;
    void exportData(List<T> data, File file) throws IOException;
    List<T> importData(File file) throws IOException;
}
