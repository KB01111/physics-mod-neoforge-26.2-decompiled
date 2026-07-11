package net.diebuddies.util.cpp;

import org.jspecify.annotations.NonNull;

public interface VirtualFileSystem {
   @NonNull
   VirtualFile getFile(@NonNull String var1);

   @NonNull
   VirtualFile getFile(@NonNull String var1, @NonNull String var2);
}
