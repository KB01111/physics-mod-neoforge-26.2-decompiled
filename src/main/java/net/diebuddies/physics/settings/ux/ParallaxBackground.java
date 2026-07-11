package net.diebuddies.physics.settings.ux;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;

public class ParallaxBackground extends AbstractWidget {
   private float transitionLength = 0.7F;
   private List<ImageElement> elements = new ObjectArrayList();
   private List<ImageElement> currentTransition;
   private ImageElement[] loadingElements;
   private float transitionTime;
   private FocusSelector focus;

   public ParallaxBackground(float x, float y, float width, float height, FocusSelector focus) {
      super((int)x, (int)y, (int)width, (int)height, null);
      this.focus = focus;
   }

   public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
      List<ImageElement> nextElements = null;
      if (this.focus != null && this.currentTransition == null) {
         Animatable focusedElement = this.focus.getFocusedElement();
         if (focusedElement != null) {
            Parallaxes parallaxes = focusedElement.getAnimator(Parallaxes.class);
            if (parallaxes != null) {
               ImageElement[] parallaxElements = parallaxes.elements;
               if (this.loadingElements != parallaxElements) {
                  if (this.loadingElements != null) {
                     for (ImageElement element : this.loadingElements) {
                        element.destroyLoadedTexture();
                     }
                  }

                  this.loadingElements = parallaxElements;
               }

               List<ImageElement> nextList = null;
               if (parallaxElements != null && parallaxElements.length > 0) {
                  boolean allLoaded = true;

                  for (ImageElement element : parallaxElements) {
                     if (!element.loadImage()) {
                        allLoaded = false;
                     }
                  }

                  if (allLoaded) {
                     nextList = new ObjectArrayList(parallaxElements);
                  }
               }

               nextElements = nextList;
            }
         }
      }

      boolean sameParallaxAsCurrent = this.elements.equals(nextElements);
      if (sameParallaxAsCurrent) {
         this.loadingElements = null;
      }

      if (this.currentTransition == null && nextElements != null && !sameParallaxAsCurrent) {
         this.currentTransition = nextElements;
         this.loadingElements = null;
         nextElements = null;
         this.transitionTime = 0.0F;
      }

      float tickAdjustedDelta = delta / 20.0F;
      this.transitionTime += tickAdjustedDelta;
      float alpha = this.transitionTime / this.transitionLength;
      boolean finishedTransition = alpha >= 1.0F;
      if (!finishedTransition || this.currentTransition == null) {
         for (ImageElement elementx : this.elements) {
            ((Animatable)elementx).setAnimAlpha(1.0F);
            elementx.extractRenderState(guiGraphics, mouseX, mouseY, delta);
         }
      }

      if (this.currentTransition != null) {
         for (ImageElement elementx : this.currentTransition) {
            ((Animatable)elementx).setAnimAlpha(Math.min(alpha, 1.0F));
            elementx.extractRenderState(guiGraphics, mouseX, mouseY, delta);
         }

         if (finishedTransition) {
            for (ImageElement elementx : this.elements) {
               elementx.destroyLoadedTexture();
            }

            this.elements = this.currentTransition;
            this.currentTransition = null;
         }
      }
   }

   public void addImageElement(ImageElement element) {
      this.elements.add(element);
   }

   public void addImageElements(ImageElement[] elements) {
      this.elements.addAll(Arrays.asList(elements));
   }

   public void removeImageElement(ImageElement element) {
      this.elements.remove(element);
   }

   public void clearImageElements() {
      this.elements.clear();
   }

   public void setTransitionLength(float transitionLength) {
      this.transitionLength = transitionLength;
   }

   public float getTransitionLength() {
      return this.transitionLength;
   }

   public void updateWidgetNarration(NarrationElementOutput narration) {
   }
}
