package module;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.mvc.fx.handlers.HoverOnHoverHandler;
import org.eclipse.gef.mvc.fx.parts.DefaultFocusFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultHoverFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultSelectionFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultSelectionHandlePartFactory;
import org.eclipse.gef.mvc.fx.policies.ViewportPolicy;
import org.eclipse.gef.mvc.fx.providers.ShapeBoundsProvider;
import org.eclipse.gef.zest.fx.ZestFxModule;
import org.eclipse.gef.zest.fx.behaviors.NodeHidingBehavior;
import org.eclipse.gef.zest.fx.behaviors.NodeLayoutBehavior;
import org.eclipse.gef.zest.fx.handlers.HideOnTypeHandler;
import org.eclipse.gef.zest.fx.handlers.ShowHiddenNeighborsOnTypeHandler;
import org.eclipse.gef.zest.fx.policies.HidePolicy;
import org.eclipse.gef.zest.fx.policies.ShowHiddenNeighborsPolicy;
import org.eclipse.gef.zest.fx.providers.NodePartAnchorProvider;

import com.google.inject.Provider;
import com.google.inject.multibindings.MapBinder;

public class CustomModule extends ZestFxModule {
	
	@Override
	protected void bindGraphPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(LayoutContext.class);
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(CustomGraphLayoutBehavior.class);
	}

	@Override
	public void bindNodePartAdapters(MapBinder adapterMapBinder) {
		// layout
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(NodeLayoutBehavior.class);
		// pruning
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(HidePolicy.class);
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ShowHiddenNeighborsPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(NodeHidingBehavior.class);

		// show hidden neighbors on-type
		adapterMapBinder.addBinding(AdapterKey.role("show-hidden-neighbors"))
				.to(ShowHiddenNeighborsOnTypeHandler.class);

		// hide on-type
		adapterMapBinder.addBinding(AdapterKey.role("hide")).to(HideOnTypeHandler.class);

		// anchor provider
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(NodePartAnchorProvider.class);

		// feedback and handles
		adapterMapBinder
				.addBinding(AdapterKey.role(DefaultSelectionHandlePartFactory.SELECTION_HANDLES_GEOMETRY_PROVIDER))
				.toProvider(new Provider<ShapeBoundsProvider>() {
					@Override
					public ShapeBoundsProvider get() {
						return new ShapeBoundsProvider(1);
					}
				});

		// selection feedback provider
		adapterMapBinder
				.addBinding(AdapterKey.role(DefaultSelectionFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
				.toProvider(new Provider<ShapeBoundsProvider>() {
					@Override
					public ShapeBoundsProvider get() {
						return new ShapeBoundsProvider(0.5);
					}
				});

		// selection link feedback provider
		adapterMapBinder
				.addBinding(
						AdapterKey.role(DefaultSelectionFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
				.toProvider(new Provider<ShapeBoundsProvider>() {
					@Override
					public ShapeBoundsProvider get() {
						return new ShapeBoundsProvider(0.5);
					}
				});

		// hover feedback provider
		adapterMapBinder.addBinding(AdapterKey.role(DefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(ShapeBoundsProvider.class);

		// focus feedback provider
		adapterMapBinder.addBinding(AdapterKey.role(DefaultFocusFeedbackPartFactory.FOCUS_FEEDBACK_GEOMETRY_PROVIDER))
				.toProvider(new Provider<ShapeBoundsProvider>() {
					@Override
					public ShapeBoundsProvider get() {
						return new ShapeBoundsProvider(0.5);
					}
				});

		// hover on-hover
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(HoverOnHoverHandler.class);

		// open Link on key-press (custom)
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(NodeHandler.class);
	}
	
	@Override
	protected void bindChangeViewportPolicyAsIRootPartAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// overwrite default zoom policy to perform semantic zooming (navigating
		// nested graphs on zoom level changes)
		adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ViewportPolicy.class);
	}

}
