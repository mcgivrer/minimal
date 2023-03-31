# Add a resource manager


_(TO BE REVIEWED)_

## Goal

As we are willing to create some fancy graphics, and maybe use some images as background and some new fonts to write some text, we are going to have manage some resources.
And those resources maybe reused accross mutiple Scene. So to manage thinly the memory used by our game, we may propose a tool to manahge and store temporarily in the java memory some of those resources.

This is where a resource manager woudl help a lot.  

It must provide a convinient way to request some resource, load and store it in a cache, and serve this resource when required.

## The proposed Design

![The class ResourceManager and its API](http://www.plantuml.com/plantuml/png/NOwzJiKm38LtFuMv4mWwi1PqLJ6062eaU08tEKsAybCbpeHuTsXXe4oMl-_OEaDLATfYe0lrYE0ro9B81AcLNr5pAmQZ974e7yGT6p4U_IZh_PkM9RcRb-aTHi-R2rdivG_cLTHVtH5ViuC-Ht4ucFwXpJCAxAv-OuyvwJ6r4VeaUf88FjsUkElEs_nVhI_7dVnYmVkLwFV3gIdg7nYXFjjA0v9jCNm1)

The class `ResourceManager` provides some useful helpers to retrieve image or font:

- `getImage(String)` will load an image and return the corresponding `BufferedImage`, if it exists,
- `getFont(String)` will load a font for text rendering, using the Java API, and return a `Font` instance.


## Implementation

the main ResourceManager class would be:

```java
public class ResoucreManager {
  private static Map<String,Object> cache = new ConcurrentHashMap<>();
  
  public static BufferedImage getImage(String path){
    if(!cache.contains(path){
      load(path);      
    }
    return (BufferedImage) cache.get(path);
  }
  
  public static Font getFont(String path){
    if(!cache.contains(path){
      load(path);      
    }
    return (Font) cache.get(path);
  }
  
  private static void load(String path){
   //...
  }
}
``` 

Here are clearely defined the *getImage* and *getFont*, but all the Intelligence remains in the *load* method. Based on the file extension, we will defined what can be done to load the corresponding resource with the right object type.

```java
public class ResourceManager {
  //...
  private static void load(String path){
   if(!path.contains(".")){
     return null;
   }
   switch(path.substring(path.findLast(".")+1,path.length - (path.findLast(".")+1)).toUppercase()){
     case "PNG","JPG","GIF" ->{
        BufferedIMage img = ImageIO.read(ResourceManager.class.getResourceAsStream(path));
        if(Optional.ofNullable(img).isPresent()){
          cache.put(path,img);
        }
     }
     case "TTF" ->{
        Fnt font = ////
        if(Optional.ofNullable(font).isPresent()){
          cache.put(path,font);
        }
     }
     default ->{
       System.err.printf("File format unknown for %s%n",path);
     }
   }
  }
}
```
