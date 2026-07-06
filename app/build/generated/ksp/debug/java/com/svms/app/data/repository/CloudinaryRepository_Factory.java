package com.svms.app.data.repository;

import com.svms.app.data.remote.CloudinaryService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class CloudinaryRepository_Factory implements Factory<CloudinaryRepository> {
  private final Provider<CloudinaryService> cloudinaryServiceProvider;

  public CloudinaryRepository_Factory(Provider<CloudinaryService> cloudinaryServiceProvider) {
    this.cloudinaryServiceProvider = cloudinaryServiceProvider;
  }

  @Override
  public CloudinaryRepository get() {
    return newInstance(cloudinaryServiceProvider.get());
  }

  public static CloudinaryRepository_Factory create(
      Provider<CloudinaryService> cloudinaryServiceProvider) {
    return new CloudinaryRepository_Factory(cloudinaryServiceProvider);
  }

  public static CloudinaryRepository newInstance(CloudinaryService cloudinaryService) {
    return new CloudinaryRepository(cloudinaryService);
  }
}
