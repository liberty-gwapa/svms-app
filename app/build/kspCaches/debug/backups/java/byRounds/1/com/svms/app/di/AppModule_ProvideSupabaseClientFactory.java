package com.svms.app.di;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.github.jan.supabase.SupabaseClient;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AppModule_ProvideSupabaseClientFactory implements Factory<SupabaseClient> {
  private final Provider<Context> contextProvider;

  public AppModule_ProvideSupabaseClientFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SupabaseClient get() {
    return provideSupabaseClient(contextProvider.get());
  }

  public static AppModule_ProvideSupabaseClientFactory create(Provider<Context> contextProvider) {
    return new AppModule_ProvideSupabaseClientFactory(contextProvider);
  }

  public static SupabaseClient provideSupabaseClient(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideSupabaseClient(context));
  }
}
