import { Component, OnInit } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';
import { ChatService } from '../shared';

import { LoginModalService, AccountService, Account } from 'app/core';

import { Observable } from 'rxjs';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';

import { IChatmessage, Chatmessage } from 'app/shared/model/chatmessage.model';
import { ChatmessageService } from '../entities/chatmessage/chatmessage.service';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['home.scss']
})
export class HomeComponent implements OnInit {
  account: Account;
  modalRef: NgbModalRef;
  messages: Array<Object> = [];
  message = '';
  private _chatmessage: IChatmessage = { userLogin: null, message: null, time: null };

  isSaving: boolean;

  constructor(
    private accountService: AccountService,
    private loginModalService: LoginModalService,
    private eventManager: JhiEventManager,
    private chatService: ChatService,
    protected chatmessageService: ChatmessageService
  ) {}

  ngOnInit() {
    this.chatService.connect();

    this.chatService.receive().subscribe(message => {
      this.messages.push(message);
    });

    this.accountService.identity().then(account => {
      this.account = account;
    });
    this.registerAuthenticationSuccess();
    this.registerLogoutSuccess();
  }

  registerAuthenticationSuccess() {
    this.eventManager.subscribe('authenticationSuccess', message => {
      this.accountService.identity().then(account => {
        this.account = account;
        this.chatService.disconnect();
        this.chatService.connect();
      });
    });
  }
  registerLogoutSuccess() {
    this.eventManager.subscribe('logoutSuccess', message => {
      this.chatService.disconnect();
      this.chatService.connect();
    });
  }

  isAuthenticated() {
    return this.accountService.isAuthenticated();
  }

  login() {
    this.modalRef = this.loginModalService.open();
  }

  sendMessage(message) {
    if (message.length === 0) {
      return;
    }
    this.chatService.sendMessage(message);
    this.chatmessage = message;
    this.save(this.chatmessage);
    this.message = '';
  }

  save(chatmessage) {
    if (this.chatmessage.id !== undefined) {
      this.subscribeToSaveResponse(this.chatmessageService.update(this.chatmessage));
    } else {
      this.subscribeToSaveResponse(this.chatmessageService.create(this.chatmessage));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IChatmessage>>) {
    result.subscribe((res: HttpResponse<IChatmessage>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
  }

  protected onSaveError() {
    this.isSaving = false;
  }

  get chatmessage() {
    return this._chatmessage;
  }

  set chatmessage(message: any) {
    this._chatmessage.message = message;
  }
}
