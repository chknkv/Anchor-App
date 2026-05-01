package com.chknkv.feature.addiction.data.mapper

import com.chknkv.corenetwork.NetworkEntity
import com.chknkv.corenetwork.api.ApiClient
import com.chknkv.corenetwork.isSuccessfulExecute
import com.chknkv.corenetwork.requireBody
import com.chknkv.feature.addiction.models.data.AddictionAllGroupsBody
import com.chknkv.feature.addiction.models.data.AddictionAllGroupsResponse
import com.chknkv.feature.addiction.models.data.AddictionCreateRequest
import com.chknkv.feature.addiction.models.data.AddictionDetailsBody
import com.chknkv.feature.addiction.models.data.AddictionDetailsResponse
import com.chknkv.feature.addiction.models.data.AddictionSelectedRequest
import com.chknkv.feature.addiction.models.data.AddictionSelectionGroupsBody
import com.chknkv.feature.addiction.models.data.AddictionSelectionGroupsResponse
import com.chknkv.feature.addiction.models.data.AddictionUpdateRequest
import io.ktor.http.HttpMethod

/**
 * Реализация [AddictionApiMapper].
 *
 * @param apiClient DSL-клиент из CoreNetwork.
 */
internal class AddictionApiMapperImpl(
    private val apiClient: ApiClient,
) : AddictionApiMapper {

    override suspend fun getAddictionGroupsForSelection(): AddictionSelectionGroupsBody =
        apiClient.request<AddictionSelectionGroupsResponse> {
            endpoint = GET_ADDICTION_GROUP_FOR_SELECTION_ENDPOINT_PATH
            method = HttpMethod.Get
        }.requireBody()

    override suspend fun saveSelectedAddictions(request: AddictionSelectedRequest) {
        apiClient.request<NetworkEntity<Unit>> {
            endpoint = POST_ADDICTION_SAVE_SELECTED_ENDPOINT_PATH
            method = HttpMethod.Post
            body = request
        }.isSuccessfulExecute()
    }

    override suspend fun getAllClientAddictions(): AddictionAllGroupsBody =
        apiClient.request<AddictionAllGroupsResponse> {
            endpoint = GET_CLIENT_ADDICTIONS_ALL_ENDPOINT_PATH
            method = HttpMethod.Get
        }.requireBody()

    override suspend fun createNewClientAddiction(request: AddictionCreateRequest) {
        apiClient.request<NetworkEntity<Unit>> {
            endpoint = POST_CLIENT_ADDICTIONS_CREATE_ENDPOINT_PATH
            method = HttpMethod.Post
            body = request
        }.isSuccessfulExecute()
    }

    override suspend fun getClientDetailsAddiction(id: Int): AddictionDetailsBody =
        apiClient.request<AddictionDetailsResponse> {
            endpoint = "$GET_CLIENT_ADDICTIONS_DETAILS_ENDPOINT_PATH/$id"
            method = HttpMethod.Get
        }.requireBody()

    override suspend fun incrementAddictionControlDays(id: Int) {
        apiClient.request<NetworkEntity<Unit>> {
            endpoint = "$POST_CLIENT_ADDICTIONS_INCREMENT/$id"
            method = HttpMethod.Post
        }.isSuccessfulExecute()
    }

    override suspend fun updateClientAddiction(id: Int, request: AddictionUpdateRequest) {
        apiClient.request<NetworkEntity<Unit>> {
            endpoint = "$PATCH_CLIENT_ADDICTIONS_UPDATE_ENDPOINT_PATH/$id"
            method = HttpMethod.Patch
            body = request
        }.isSuccessfulExecute()
    }

    override suspend fun deleteClientAddiction(id: Int) {
        apiClient.request<NetworkEntity<Unit>> {
            endpoint = "$DELETE_CLIENT_ADDICTIONS_DELETE_ENDPOINT_PATH/$id"
            method = HttpMethod.Delete
        }.isSuccessfulExecute()
    }

    companion object {
        private const val GET_ADDICTION_GROUP_FOR_SELECTION_ENDPOINT_PATH = "addictions/default-selection-groups"
        private const val POST_ADDICTION_SAVE_SELECTED_ENDPOINT_PATH = "addictions/save-selected"
        private const val GET_CLIENT_ADDICTIONS_ALL_ENDPOINT_PATH = "client/addictions/all"
        private const val POST_CLIENT_ADDICTIONS_CREATE_ENDPOINT_PATH = "client/addictions/create"
        private const val GET_CLIENT_ADDICTIONS_DETAILS_ENDPOINT_PATH = "client/addictions/details"
        private const val POST_CLIENT_ADDICTIONS_INCREMENT = "client/addictions/increment"
        private const val PATCH_CLIENT_ADDICTIONS_UPDATE_ENDPOINT_PATH = "client/addictions/update"
        private const val DELETE_CLIENT_ADDICTIONS_DELETE_ENDPOINT_PATH = "client/addictions/delete"
    }
}
