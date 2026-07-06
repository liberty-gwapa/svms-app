package com.svms.app.data.repository;

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
public final class ViolationRepository_Factory implements Factory<ViolationRepository> {
  private final Provider<SupabaseClient> supabaseClientProvider;

  public ViolationRepository_Factory(Provider<SupabaseClient> supabaseClientProvider) {
    this.supabaseClientProvider = supabaseClientProvider;
  }

  @Override
  public ViolationRepository get() {
    return newInstance(supabaseClientProvider.get());
  }

  public static ViolationRepository_Factory create(
      Provider<SupabaseClient> supabaseClientProvider) {
    return new ViolationRepository_Factory(supabaseClientProvider);
  }

  public static ViolationRepository newInstance(SupabaseClient supabaseClient) {
    return new ViolationRepository(supabaseClient);
  }
}
