package net.diebuddies.physics.settings.gui;

import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.cloth.LabelEntry;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.minecraft.client.Minecraft;

public class EnumSelectionList extends LegacyObjectSelectionList<BaseEntry> {
   public String filter = "";
   private Enum<?> selectedEnum;

   public EnumSelectionList(Minecraft minecraft, int i, int j, int k, int l, int m, Enum<?> selectedEnum) {
      super(minecraft, i, j, k, l, m);
      this.selectedEnum = selectedEnum;
      this.refreshEntries();
   }

   public void refreshEntries() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.struct.gen.VarType.isGeneric()" because "newRet" is null
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.getInferredExprType(InvocationExprent.java:634)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.getInferredExprType(FunctionExprent.java:243)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:966)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.AssignmentExprent.toJava(AssignmentExprent.java:154)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.listToJava(ExprProcessor.java:895)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.BasicBlockStatement.toJava(BasicBlockStatement.java:90)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.SequenceStatement.toJava(SequenceStatement.java:107)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.RootStatement.toJava(RootStatement.java:36)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.writeMethod(ClassWriter.java:1283)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokevirtual net/diebuddies/physics/settings/gui/EnumSelectionList.clearEntries ()V
      // 04: aconst_null
      // 05: astore 1
      // 06: aload 0
      // 07: getfield net/diebuddies/physics/settings/gui/EnumSelectionList.selectedEnum Ljava/lang/Enum;
      // 0a: invokevirtual java/lang/Enum.getDeclaringClass ()Ljava/lang/Class;
      // 0d: invokevirtual java/lang/Class.getEnumConstants ()[Ljava/lang/Object;
      // 10: checkcast [Ljava/lang/Enum;
      // 13: astore 2
      // 14: aload 2
      // 15: astore 3
      // 16: aload 3
      // 17: arraylength
      // 18: istore 4
      // 1a: bipush 0
      // 1b: istore 5
      // 1d: iload 5
      // 1f: iload 4
      // 21: if_icmpge 70
      // 24: aload 3
      // 25: iload 5
      // 27: aaload
      // 28: astore 6
      // 2a: invokestatic net/minecraft/locale/Language.getInstance ()Lnet/minecraft/locale/Language;
      // 2d: aload 6
      // 2f: invokevirtual java/lang/Enum.toString ()Ljava/lang/String;
      // 32: invokevirtual net/minecraft/locale/Language.getOrDefault (Ljava/lang/String;)Ljava/lang/String;
      // 35: astore 7
      // 37: aload 7
      // 39: aload 0
      // 3a: getfield net/diebuddies/physics/settings/gui/EnumSelectionList.filter Ljava/lang/String;
      // 3d: invokevirtual java/lang/String.toLowerCase ()Ljava/lang/String;
      // 40: invokevirtual java/lang/String.contains (Ljava/lang/CharSequence;)Z
      // 43: ifne 49
      // 46: goto 6a
      // 49: new net/diebuddies/physics/settings/cloth/LabelEntry
      // 4c: dup
      // 4d: aload 0
      // 4e: aload 7
      // 50: invokespecial net/diebuddies/physics/settings/cloth/LabelEntry.<init> (Lnet/diebuddies/physics/settings/gui/legacy/LegacyObjectSelectionList;Ljava/lang/String;)V
      // 53: astore 8
      // 55: aload 8
      // 57: aload 6
      // 59: invokevirtual net/diebuddies/physics/settings/cloth/LabelEntry.setUserData (Ljava/lang/Object;)V
      // 5c: aload 0
      // 5d: aload 8
      // 5f: invokevirtual net/diebuddies/physics/settings/gui/EnumSelectionList.addEntry (Lnet/diebuddies/physics/settings/gui/legacy/LegacyAbstractSelectionList$LegacyEntry;)I
      // 62: pop
      // 63: aload 1
      // 64: ifnonnull 6a
      // 67: aload 8
      // 69: astore 1
      // 6a: iinc 5 1
      // 6d: goto 1d
      // 70: aload 1
      // 71: ifnull 79
      // 74: aload 0
      // 75: aload 1
      // 76: invokevirtual net/diebuddies/physics/settings/gui/EnumSelectionList.ensureVisible (Lnet/diebuddies/physics/settings/gui/legacy/LegacyAbstractSelectionList$LegacyEntry;)V
      // 79: return
   }
}
