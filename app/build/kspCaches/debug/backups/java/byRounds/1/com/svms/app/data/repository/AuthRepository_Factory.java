package com.svms.app.data.repository;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.github.jan.supabase.SupabaseClient;
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
public final class AuthRepository_Factory implements Factory<AuthRepository> {
  private final Provider<SupabaseClient> supabaseClientProvider;

  private final Provider<Context> contextProvider;

  public AuthRepository_Factory(Provider<SupabaseClient> supabaseClientProvider,
      Provider<Context> contextProvider) {
    this.supabaseClientProvider = supabaseClientProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public AuthRepository get() {
    return newInstance(supabaseClientProvider.get(), contextProvider.get());
  }

  public static AuthRepository_Factory create(Provider<SupabaseClient> supabaseClientProvider,
      Provider<Context> contextProvider) {
    return new AuthRepository_Factory(supabaseClientProvider, contextProvider);
  }

  public static AuthRepository newInstance(SupabaseClient supabaseClient, Context context) {
    return new AuthRepository(supabaseClient, context);
  }
}
