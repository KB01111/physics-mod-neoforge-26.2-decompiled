package net.diebuddies.model;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class XmlNode {
   private String name;
   private Map<String, String> attributes;
   private String data;
   private Map<String, List<XmlNode>> children;

   public XmlNode(String name) {
      this.name = name;
   }

   public void addAttribute(String name, String value) {
      this.getAttributes().put(name, value);
   }

   public Map<String, String> getAttributes() {
      if (this.attributes == null) {
         this.attributes = new LinkedHashMap<>();
      }

      return this.attributes;
   }

   public void setData(String data) {
      this.data = data;
   }

   public String getData() {
      return this.data;
   }

   public String getName() {
      return this.name;
   }

   public String getAttribute(String name) {
      return this.getAttributes().get(name);
   }

   public void addChild(XmlNode node) {
      this.getChildren(node.getName()).add(node);
   }

   public List<XmlNode> getChildren(String name) {
      if (this.children == null) {
         this.children = new LinkedHashMap<>();
      }

      List<XmlNode> list = this.children.get(name);
      if (list == null) {
         list = new ObjectArrayList();
         this.children.put(name, list);
      }

      return list;
   }

   public XmlNode getChildWithAttribute(String child, String attribute, String value) {
      return this.getChildWithAttribute(child, attribute, value, true);
   }

   public XmlNode getChildWithAttribute(String child, String attribute, String value, boolean ignoreCase) {
      List<XmlNode> children = this.getChildren(child);

      for (int i = 0; i < children.size(); i++) {
         String searchAttribute = children.get(i).getAttribute(attribute);
         if (searchAttribute != null) {
            if (ignoreCase && value.equalsIgnoreCase(searchAttribute)) {
               return children.get(i);
            }

            if (!ignoreCase && value.equals(searchAttribute)) {
               return children.get(i);
            }
         }
      }

      return null;
   }

   public XmlNode getChild(String name) {
      List<XmlNode> nodes = this.getChildren(name);
      return !nodes.isEmpty() ? nodes.get(0) : null;
   }

   private Map<String, List<XmlNode>> getChildren() {
      if (this.children == null) {
         this.children = new Object2ObjectOpenHashMap();
      }

      return this.children;
   }

   @Override
   public String toString() {
      return this.generateString(0);
   }

   public String generateString(int spaces) {
      String s = this.getSpace(spaces) + "<" + this.name;

      for (Entry<String, String> entry : this.getAttributes().entrySet()) {
         s = s + " " + entry.getKey() + "=\"" + entry.getValue() + "\"";
      }

      if (this.data != null) {
         s = s + ">";
         s = s + this.data;
         if (this.getChildren().values().isEmpty()) {
            s = s + "</" + this.name + ">";
         }
      } else if (this.getChildren().values().isEmpty()) {
         s = s + "/>";
      } else {
         s = s + ">";
      }

      List<String> sortedKeys = new ObjectArrayList(this.getChildren().keySet());
      Collections.sort(sortedKeys);

      for (String key : sortedKeys) {
         List<XmlNode> list = this.getChildren(key);

         for (int i = 0; i < list.size(); i++) {
            s = s + "\n";
            s = s + list.get(i).generateString(spaces + 1);
         }
      }

      if (!this.getChildren().values().isEmpty()) {
         s = s + "\n" + this.getSpace(spaces) + "</" + this.name + ">";
      }

      return s;
   }

   public boolean hasChildren() {
      return !this.getChildren().isEmpty();
   }

   public String getSpace(int count) {
      String space = "";

      for (int i = 0; i < count; i++) {
         space = space + " ";
      }

      return space;
   }
}
