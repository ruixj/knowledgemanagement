import { mount } from '@vue/test-utils';
import FileManager from '../FileManager.vue';
import { InboxOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';

describe('FileManager.vue', () => {
  let wrapper;

  beforeEach(() => {
    wrapper = mount(FileManager, {
      global: {
        components: {
          InboxOutlined,
        },
      },
    });
  });

  afterEach(() => {
    wrapper.unmount();
  });

  describe('Search functionality', () => {
    it('should filter files by name', async () => {
      await wrapper.setData({
        files: [
          { id: 1, name: 'file1.txt', size: 100, type: 'doc', uploader: 'user1', uploadTime: '2023-01-01' },
          { id: 2, name: 'file2.jpg', size: 200, type: 'image', uploader: 'user2', uploadTime: '2023-01-02' },
        ],
        searchForm: {
          fileName: 'file1',
        },
      });

      expect(wrapper.vm.filteredFiles).toHaveLength(1);
      expect(wrapper.vm.filteredFiles[0].name).toBe('file1.txt');
    });

    it('should handle empty search', async () => {
      await wrapper.setData({
        files: [
          { id: 1, name: 'file1.txt', size: 100, type: 'doc', uploader: 'user1', uploadTime: '2023-01-01' },
          { id: 2, name: 'file2.jpg', size: 200, type: 'image', uploader: 'user2', uploadTime: '2023-01-02' },
        ],
        searchForm: {
          fileName: '',
        },
      });

      expect(wrapper.vm.filteredFiles).toHaveLength(2);
    });
  });

  describe('File upload', () => {
    it('should show upload modal when clicking upload button', async () => {
      await wrapper.find('.upload-btn').trigger('click');
      expect(wrapper.vm.uploadModalVisible).toBe(true);
    });

    it('should handle file removal', async () => {
      await wrapper.setData({
        fileList: [{ uid: '1', name: 'test.txt', status: 'done' }],
      });

      wrapper.vm.handleRemove({ uid: '1' });
      expect(wrapper.vm.fileList).toHaveLength(0);
    });

    it('should simulate upload progress', async () => {
      jest.useFakeTimers();
      await wrapper.setData({
        fileList: [{ uid: '1', name: 'test.txt', status: 'uploading', percent: 0 }],
      });

      wrapper.vm.handleUpload();
      jest.advanceTimersByTime(300);
      expect(wrapper.vm.fileList[0].percent).toBe(10);
      jest.clearAllTimers();
    });
  });

  describe('File download and delete', () => {
    it('should emit download event', async () => {
      const mockFile = { id: 1, name: 'file1.txt', size: 100, type: 'doc', uploader: 'user1', uploadTime: '2023-01-01' };
      wrapper.vm.downloadFile(mockFile);
      // Add assertions for download logic here
    });

    it('should emit delete event', async () => {
      const mockFile = { id: 1, name: 'file1.txt', size: 100, type: 'doc', uploader: 'user1', uploadTime: '2023-01-01' };
      wrapper.vm.deleteFile(mockFile);
      // Add assertions for delete logic here
    });
  });

  describe('File type and size formatting', () => {
    it('should correctly identify file type', () => {
      expect(wrapper.vm.getFileType('test.jpg')).toBe('image');
      expect(wrapper.vm.getFileType('test.pdf')).toBe('doc');
      expect(wrapper.vm.getFileType('test.mp3')).toBe('audio');
    });

    it('should format file size', () => {
      expect(wrapper.vm.formatFileSize(1024)).toBe('1 KB');
      expect(wrapper.vm.formatFileSize(1048576)).toBe('1 MB');
    });
  });
});