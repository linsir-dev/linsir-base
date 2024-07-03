package com.linsir.base.core.tool.convert;

import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

/**
 * 类型 转换 服务，添加了 IEnum 转换
 *
 * @author L.cm
 */
public class LinsirConversionService extends ApplicationConversionService {
	@Nullable
	private static volatile LinsirConversionService SHARED_INSTANCE;

	public LinsirConversionService() {
		this(null);
	}

	public LinsirConversionService(@Nullable StringValueResolver embeddedValueResolver) {
		super(embeddedValueResolver);
		super.addConverter(new EnumToStringConverter());
		super.addConverter(new StringToEnumConverter());
	}

	/**
	 * Return a shared default application {@code ConversionService} instance, lazily
	 * building it once needed.
	 * <p>
	 * Note: This method actually returns an {@link LinsirConversionService}
	 * instance. However, the {@code ConversionService} signature has been preserved for
	 * binary compatibility.
	 *
	 * @return the shared {@code BladeConversionService} instance (never{@code null})
	 */
	public static GenericConversionService getInstance() {
		LinsirConversionService sharedInstance = LinsirConversionService.SHARED_INSTANCE;
		if (sharedInstance == null) {
			synchronized (LinsirConversionService.class) {
				sharedInstance = LinsirConversionService.SHARED_INSTANCE;
				if (sharedInstance == null) {
					sharedInstance = new LinsirConversionService();
					LinsirConversionService.SHARED_INSTANCE = sharedInstance;
				}
			}
		}
		return sharedInstance;
	}

}
