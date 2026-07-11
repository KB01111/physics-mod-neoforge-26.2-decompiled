package net.diebuddies.util.cpp;

import java.io.IOException;
import org.jspecify.annotations.NonNull;

public interface VirtualFile {
   boolean isFile();

   @NonNull
   String getPath();

   @NonNull
   String getName();

   VirtualFile getParentFile();

   @NonNull
   VirtualFile getChildFile(String var1);

   @NonNull
   Source getSource() throws IOException;
}
