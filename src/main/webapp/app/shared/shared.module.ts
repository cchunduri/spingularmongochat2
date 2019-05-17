import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import {
  Spingularmongochat2SharedLibsModule,
  Spingularmongochat2SharedCommonModule,
  JhiLoginModalComponent,
  HasAnyAuthorityDirective
} from './';

@NgModule({
  imports: [Spingularmongochat2SharedLibsModule, Spingularmongochat2SharedCommonModule],
  declarations: [JhiLoginModalComponent, HasAnyAuthorityDirective],
  entryComponents: [JhiLoginModalComponent],
  exports: [Spingularmongochat2SharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class Spingularmongochat2SharedModule {
  static forRoot() {
    return {
      ngModule: Spingularmongochat2SharedModule
    };
  }
}
