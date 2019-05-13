import { NgbDateAdapter } from '@ng-bootstrap/ng-bootstrap';
import { NgbDateMomentAdapter } from './util/datepicker-adapter';

import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import {
  Spingularmongochat2SharedLibsModule,
  Spingularmongochat2SharedCommonModule,
  JhiLoginModalComponent,
  HasAnyAuthorityDirective,
  ChatService
} from './';

@NgModule({
  imports: [Spingularmongochat2SharedLibsModule, Spingularmongochat2SharedCommonModule],
  declarations: [JhiLoginModalComponent, HasAnyAuthorityDirective],
  providers: [{ provide: NgbDateAdapter, useClass: NgbDateMomentAdapter }, ChatService],
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
