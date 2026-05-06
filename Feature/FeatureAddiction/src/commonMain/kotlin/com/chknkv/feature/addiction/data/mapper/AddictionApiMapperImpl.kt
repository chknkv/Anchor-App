package com.chknkv.feature.addiction.data.mapper

import com.chknkv.corenetwork.api.ApiClient
import com.chknkv.feature.addiction.models.data.AddictionAllGroupsResponse
import com.chknkv.feature.addiction.models.data.AddictionCreateRequest
import com.chknkv.feature.addiction.models.data.AddictionDetailsResponse
import com.chknkv.feature.addiction.models.data.AddictionSelectedRequest
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

    override suspend fun getAddictionGroupsForSelection(): AddictionSelectionGroupsResponse =
        apiClient.execute<AddictionSelectionGroupsResponse> {
            endpoint = GET_ADDICTION_GROUP_FOR_SELECTION_ENDPOINT_PATH
            method = HttpMethod.Get
        }

    override suspend fun saveSelectedAddictions(request: AddictionSelectedRequest): Unit =
        apiClient.execute<Unit> {
            endpoint = POST_ADDICTION_SAVE_SELECTED_ENDPOINT_PATH
            method = HttpMethod.Post
            body = request
        }

    override suspend fun getAllClientAddictions(): AddictionAllGroupsResponse =
        apiClient.execute<AddictionAllGroupsResponse> {
            endpoint = GET_CLIENT_ADDICTIONS_ALL_ENDPOINT_PATH
            method = HttpMethod.Get
        }

    override suspend fun createNewClientAddiction(request: AddictionCreateRequest): Unit =
        apiClient.execute<Unit> {
            endpoint = POST_CLIENT_ADDICTIONS_CREATE_ENDPOINT_PATH
            method = HttpMethod.Post
            body = request
        }

    override suspend fun getClientDetailsAddiction(id: Int): AddictionDetailsResponse =
        apiClient.execute<AddictionDetailsResponse> {
            endpoint = "$GET_CLIENT_ADDICTIONS_DETAILS_ENDPOINT_PATH/$id"
            method = HttpMethod.Get
        }

    override suspend fun incrementAddictionControlDays(id: Int): Unit =
        apiClient.execute<Unit> {
            endpoint = "$POST_CLIENT_ADDICTIONS_INCREMENT/$id"
            method = HttpMethod.Post
        }

    override suspend fun updateClientAddiction(id: Int, request: AddictionUpdateRequest): Unit =
        apiClient.execute<Unit> {
            endpoint = "$PATCH_CLIENT_ADDICTIONS_UPDATE_ENDPOINT_PATH/$id"
            method = HttpMethod.Patch
            body = request
        }

    override suspend fun deleteClientAddiction(id: Int): Unit =
        apiClient.execute<Unit> {
            endpoint = "$DELETE_CLIENT_ADDICTIONS_DELETE_ENDPOINT_PATH/$id"
            method = HttpMethod.Delete
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
