package org.arquillian.example.ui.utils;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.arquillian.example.controller.BeerAdvisorController;
import org.arquillian.example.domain.Beer;
import org.arquillian.example.repository.BeerRepository;
import org.arquillian.example.repository.JpaBeerRepository;
import org.arquillian.example.service.BeerService;
import org.arquillian.example.stub.SkeletonBeerRepository;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

public class Deployments
{
   private static final String WEBAPP_SRC = "src/main/webapp";

   /**
    * Here we don't have real BeerRepository implementation, so we use {@link SkeletonBeerRepository stub}.
    * This is used to illustrate the idea of "walking skeleton" from the book
    * "Growing Object Oriented Software guided by tests".
    *
    * @return
    */
   public static WebArchive createSkeleton()
   {
      final MavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class)
            .loadMetadataFromPom("pom.xml")
            .goOffline();

      return addWebResourcesTo(ShrinkWrap.create(WebArchive.class, "beer-advisor.war"))
            .addPackages(true, Beer.class.getPackage(),
                  BeerService.class.getPackage(),
                  BeerAdvisorController.class.getPackage())
                  .addPackages(true, Filters.exclude(JpaBeerRepository.class),
                        BeerRepository.class.getPackage())
                        .addPackages(true, SkeletonBeerRepository.class.getPackage()) // Repository stub
                        .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                        .addAsLibraries(resolver.artifact("com.google.guava:guava").resolveAsFiles());
   }

   /**
    * In this case we have "feature complete" web app.
    */
   public static WebArchive create()
   {
      final MavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class)
                                                                     .loadMetadataFromPom("pom.xml")
                                                                     .goOffline();

      return addWebResourcesTo(ShrinkWrap.create(WebArchive.class, "beer-advisor.war"))
            .addPackages(true, Beer.class.getPackage(),
                               BeerService.class.getPackage(),
                               BeerAdvisorController.class.getPackage())
            .addPackages(true, BeerRepository.class.getPackage())
            .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
            .addAsLibraries(resolver.artifact("com.google.guava:guava").resolveAsFiles());
   }

   private static WebArchive addWebResourcesTo(WebArchive archive)
   {
      final File webAppDirectory = new File(WEBAPP_SRC);
      for (File file : FileUtils.listFiles(webAppDirectory, null, true))
      {
         if (!file.isDirectory())
         {
            archive.addAsWebResource(file, file.getPath().substring(WEBAPP_SRC.length()));
         }
      }
      return archive;
   }
}
