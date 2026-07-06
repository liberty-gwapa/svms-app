package com.svms.app.di;

import com.svms.app.data.remote.CloudinaryService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class AppModule_ProvideCloudinaryServiceFactory implements Factory<CloudinaryService> {
  private final Provider<OkHttpClient> okHttpClientProvider;

  public AppModule_ProvideCloudinaryServiceFactory(Provider<OkHttpClient> okHttpClientProvider) {
    this.okHttpClientProvider = okHttpClientProvider;
  }

  @Override
  public CloudinaryService get() {
    return provideCloudinaryService(okHttpClientProvider.get());
  }

  public static AppModule_ProvideCloudinaryServiceFactory create(
      Provider<OkHttpClient> okHttpClientProvider) {
    return new AppModule_ProvideCloudinaryServiceFactory(okHttpClientProvider);
  }

  public static CloudinaryService provideCloudinaryService(OkHttpClient okHttpClient) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideCloudinaryService(okHttpClient));
  }
}
