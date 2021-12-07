package com.perfma.xlab.xpocket.scroll.compile;

import com.perfma.xlab.xpocket.scroll.Scroll;
import com.perfma.xlab.xpocket.scroll.ScrollConstants;
import com.perfma.xlab.xpocket.scroll.utils.BytesUtils;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class ScrollCompiler {
    
    private static final Map<String,ScrollScriptCompiler> compilers = new ConcurrentHashMap<>();
    
    static {
        Iterator<ScrollScriptCompiler> compilersIt = ServiceLoader.load(ScrollScriptCompiler.class).iterator();
        while (compilersIt.hasNext()) {
            ScrollScriptCompiler compiler = compilersIt.next();
            compilers.putIfAbsent(compiler.name(), compiler);
        } 
    }
    
    public List<byte[]> compile(String scriptName,String script) {
        ScrollScriptCompiler compiler = compilers.get(scriptName.toUpperCase());
        return compiler.compile(UUID.randomUUID().toString(), script);
    }
    
    public void compile(Scroll scroll,
            OutputStream outputStream) {
        List<byte[]> content = scroll.getScripts();
        
        try {
            DataOutputStream writer = new DataOutputStream(outputStream);
            
            //HEADER 16 bytes
            writer.write(ScrollConstants.MAGIC_CODE);
            writer.writeShort(0);
            writer.writeLong(scroll.getVersion());
            
            //name & namespace
            byte[] nameBytes = scroll.getName().getBytes(Charset.forName("UTF-8"));
            byte[] nameSpaceBytes = scroll.getNamespace().toUpperCase().getBytes(Charset.forName("UTF-8"));
            writer.writeLong(nameBytes.length);
            writer.write(nameBytes);
            writer.writeLong(nameSpaceBytes.length);
            writer.write(nameSpaceBytes);
            writer.write(ScrollConstants.GAP_BYTES, 0, 
                    BytesUtils.paddingSize(8 + 8 + nameBytes.length 
                            + nameSpaceBytes.length));
            
            //validation
            writer.writeLong(0L);
            
            //info
            writer.writeLong(0L);
            
            //script content
            //type content
            byte[] scriptNameBytes = scroll.getScriptName().toUpperCase().getBytes(Charset.forName("UTF-8"));
            int length = scriptNameBytes.length;
            int padding = BytesUtils.paddingSize(length);
            writer.writeLong(length);
            writer.write(scriptNameBytes);
            writer.write(ScrollConstants.GAP_BYTES, 0, padding);
            
            //script content header
            writer.writeLong(0);
            
            //script content
            length = 0;
            for(byte[] b : content) {
                length += 8;
                length += b.length;
            }
            padding = BytesUtils.paddingSize(length);
            
            writer.writeLong(length);
            
            for(byte[] b : content) {
                writer.writeLong(b.length);
                writer.write(b);
            }
            
            writer.write(ScrollConstants.GAP_BYTES, 0, padding);
            
            writer.flush();
            
        } catch (Throwable ex) {
            ex.printStackTrace();
        }     
    }

    private static int contentLength() {
        int length = 0;
        
        int header = 16;
        length += header;
        
        int validataBlock = 8;
        length += validataBlock;
        
        int infoBlock = 8;
        length += infoBlock;
        
        return length;
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        
        ScrollCompiler scrollCompiler = new ScrollCompiler();
        File file =  new File("D:\\opensource\\xlab\\XPocket\\xpocket-scroll//test.scl");
        file.createNewFile();
        
        String name = "test";
        String namespace = "gongyu";
        
        String scriptName = "groovy";
        String script = "System.out.println(5*3);";
        
        Scroll scroll = new Scroll();
        scroll.setName(name);
        scroll.setNamespace(namespace);
        scroll.setVersion(ScrollConstants.VERSION);
        
        scroll.setScriptName(scriptName);
        scroll.setScripts(scrollCompiler.compile(scriptName, script));
        
        
        scrollCompiler.compile(scroll,new FileOutputStream(file));

        
        
    }
}
