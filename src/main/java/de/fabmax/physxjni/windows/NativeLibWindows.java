package de.fabmax.physxjni.windows;

import de.fabmax.physxjni.NativeLib;
import java.util.ArrayList;
import java.util.List;

public class NativeLibWindows extends NativeLib {
   private static final String version = "2.5.1";
   private static final List<String> libraries = new ArrayList<String>() {
      {
         this.add("PhysX_64.dll");
         this.add("PhysXCommon_64.dll");
         this.add("PhysXCooking_64.dll");
         this.add("PhysXFoundation_64.dll");
         this.add("PhysXJniBindings_64.dll");
      }
   };

   @Override
   public String getVersion() {
      return "2.5.1";
   }

   @Override
   protected List<String> getLibResourceNames() {
      return libraries;
   }
}
