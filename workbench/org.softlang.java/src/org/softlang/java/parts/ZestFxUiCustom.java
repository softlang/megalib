package org.softlang.java.parts;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.zest.fx.ZestFxModule;
import org.eclipse.gef.zest.fx.ui.ZestFxUiModule;

import com.google.inject.multibindings.MapBinder;

public class ZestFxUiCustom extends ZestFxModule{
	
	@Override
	public void bindNodePartAdapters(MapBinder binder) {
		super.bindNodePartAdapters(binder);
		binder.addBinding(AdapterKey.defaultRole()).to(NodeHandler.class);
	}
	
	
	
}
