package com.joyplus.Service.Return;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
 * {
 "video": [
 {
 "prod_id": 节目id
 "prod_name": 节目名
 "prod_type": 节目类型
 "prod_pic_url": 海报
 },
 ]
 } 
 */
@JsonIgnoreProperties
public class ReturnVideoVideo {

	public Video[] video;

	public static class Video {

		public String prod_id;
		public String prod_name;
		public String prod_type;
		public String prod_pic_url;
		public String score;

	}
}
