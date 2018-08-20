package module;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.zest.fx.ZestFxModule;
import com.google.inject.multibindings.MapBinder;

public class CustomModule extends ZestFxModule{
	
	@Override
	public void bindNodePartAdapters(MapBinder mapBinder){
		super.bindNodePartAdapters(mapBinder);
		mapBinder.addBinding(AdapterKey.defaultRole()).to(NodeHandler.class);
	}

}
