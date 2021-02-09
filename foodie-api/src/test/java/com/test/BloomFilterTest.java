package com.test;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.junit.Test;

import java.nio.charset.Charset;

public class BloomFilterTest {

    @Test
    public void myTest() {
        BloomFilter bf = BloomFilter.create(Funnels.stringFunnel(Charset.forName("utf-8")), 100000);

        /*bf.put("1001");
        bf.mightContain();*/

        for(int i=0;i<100000;i++){
            bf.put(String.valueOf(i));
        }

        int counts = 0;
        for(int i=0;i<10000;i++){
            boolean isExist = bf.mightContain("imooc" + i);
            if(isExist){
                counts ++;
            }
        }

        System.out.println("误判率" + counts);
    }
}
