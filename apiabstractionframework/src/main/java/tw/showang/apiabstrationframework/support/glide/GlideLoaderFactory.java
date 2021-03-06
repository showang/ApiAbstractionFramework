package tw.showang.apiabstrationframework.support.glide;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoaderFactory;

import java.io.InputStream;

public interface GlideLoaderFactory {

	ModelLoaderFactory<GlideUrl, InputStream> createUrlLoaderFactory();

}
