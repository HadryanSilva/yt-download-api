package br.com.hadryan.api.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DownloadPostRequest {

    private String url;
    private String resolution;

}
