/*
 * Copyright 2014 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.jpa.sync.client.local;

import org.jboss.errai.jpa.sync.client.shared.SyncResponses;

/**
 * A callback used by {@link ClientSyncWorker}.
 * 
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 * @author Christian Sadilek <csadilek@redhat.com>
 * 
 * @param <E>
 *          The entity type the worker's named query returns.
 */
public interface DataSyncCallback<E> {

  /**
   * Invoked when a synchronization operation has completed.
   * 
   * @param responses
   *          result of the sync operation, never null.
   */
  public void onSync(SyncResponses<E> responses);
}
