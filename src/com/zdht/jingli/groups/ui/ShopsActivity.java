package com.zdht.jingli.groups.ui;

import java.util.ArrayList;
import java.util.HashMap;

import com.zdht.jingli.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
/**
 * <掌上锦里>
 * 
 * @author luchengsong
 */
public class ShopsActivity extends SCBaseActivity implements OnClickListener,
		OnItemClickListener {
	private Context context;
	private TextView tv_shops_login;
	/**
	 * 九宫格菜单
	 */
	private GridView gv_shops;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shops);
		context = this;
		/**
		 * 初始化View
		 */
		intView();
		/**
		 * 加载九宫格内容
		 */
		fillMenuGridView();
	}

	private void intView() {
		tv_shops_login = (TextView) findViewById(R.id.shops_title_login);
		tv_shops_login.setOnClickListener(this);
		gv_shops = (GridView) findViewById(R.id.gv_shops);
		gv_shops.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int nId = v.getId();
		if (nId == R.id.shops_title_login) {
			LoginActivity.launch(this);
			return;
		}

	}

	private void fillMenuGridView() {
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();

		/* 特色餐饮 */
		HashMap<String, Object> specialfoodMap = new HashMap<String, Object>();
		specialfoodMap.put("ItemImage", R.drawable.gv_specialfood_xml);
		listItem.add(specialfoodMap);

		/* 小吃街 */
		HashMap<String, Object> snackstreetMap = new HashMap<String, Object>();
		snackstreetMap.put("ItemImage", R.drawable.gv_snackstreet_xml);
		listItem.add(snackstreetMap);

		/* 酒吧街 */
		HashMap<String, Object> barstreetMap = new HashMap<String, Object>();
		barstreetMap.put("ItemImage", R.drawable.gv_barstreet_xml);
		listItem.add(barstreetMap);

		/* 锦里客栈 */
		HashMap<String, Object> hotelMap = new HashMap<String, Object>();
		hotelMap.put("ItemImage", R.drawable.gv_hotel_xml);
		listItem.add(hotelMap);

		/* 名俗商品 */
		HashMap<String, Object> commongoodsMap = new HashMap<String, Object>();
		commongoodsMap.put("ItemImage", R.drawable.gv_commongoods_xml);
		listItem.add(commongoodsMap);

		SimpleAdapter menuAdapter = new SimpleAdapter(context, listItem,
				R.layout.menu_gridview_item, new String[] { "ItemImage" },
				new int[] { R.id.menu_GridView_icon });
		gv_shops.setAdapter(menuAdapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		 switch (parent.getId())
	        {
	        
	            
	            case R.id.gv_shops:
	                /**
	                 * 九宫格菜单
	                 */
	                switch (position)
	                {
	                    case 0:
	                        /**
	                         * 特色餐饮
	                         */
	                       // startActivity(new Intent(context, ThemeSongRecomActivity.class));
	                        break;
	                    
	                    case 1:
	                        /**
	                         * 小吃街
	                         */
	                       // startActivity(new Intent(context, RankListActivityGroup.class));
	                        break;
	                    
	                    case 2:
	                        /**
	                         * 酒吧街
	                         */
	                       // startActivity(new Intent(context, MusicCategorActivity.class));
	                        break;
	                    
	                    case 3:
	                        /**
	                         * 锦里客栈
	                         */
	                      //  startActivity(new Intent(context, MusicRadioActivity.class));
	                        break;
	                    
	                    case 4:
	                        /**
	                         * 名俗商品
	                         */
	                      //  startActivity(new Intent(context, SpecialCommendActivity.class));
	                        break;
	                    
	  
	                    
	                    default:
	                        break;
	                }
	                break;
	            
	            default:
	                break;
	        }
	}

}
