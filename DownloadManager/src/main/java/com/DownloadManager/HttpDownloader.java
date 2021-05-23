package com.DownloadManager;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
public class HttpDownloader {
    private String URL;
    private int threadsNum;


    HttpDownloadUtility download;

    @GetMapping(value = "/index")
    public String index() {
        System.out.println("indexxx");
        return "index";
    }

    @RequestMapping("/add")
    public ModelAndView result(@RequestParam("url") String url, @RequestParam("num") int num, ModelAndView model) throws IOException, InterruptedException {
        URL = url;
        threadsNum = num;
        download = new HttpDownloadUtility(num);
        String saveDir = "src\\main\\resources";

        if (download.checkUrl(url))
            download.resume(url);
        else
            download.downloadFile(url, saveDir, num);


        model.addObject("url", url);
        model.addObject("num", num);
        model.setViewName("index");
        return model;
    }

    @RequestMapping("/resume")
    public ResponseEntity resume(@RequestParam(value = "url", required = false) String url) throws IOException, InterruptedException {

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping("/pause")
    public String pause() throws InterruptedException, IOException {
        download.pause();
        return "index";
    }


}