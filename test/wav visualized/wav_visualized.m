close all;
clear all;

wav_files = dir('*.wav');
audio = {};
Fs = [];
for
i = 1
:
length(wav_files)
[newaudio, fs] = audioread(wav_files(i).name);
audio = [audio, newaudio];
disp(fs);
Fs = cat(1, Fs, fs);
end
        figure(1);

for
i = 1
:
length(wav_files)
/2
subplot(4,1,i);
dt = 1 / Fs(i);
final = (length(audio{i}) * dt) - dt;
t = 0
:dt:
final;
plot(t, audio{i}
);
title(wav_files(i)
.name)
xlim([0,final]);
ylim([-1,1]);
xticks(0:0.5:final);


end
        figure(2);
for
i = 5
:
length(wav_files)
subplot(4,1,i-4);
dt = 1 / Fs(i);
final = (length(audio{i}) * dt) - dt;
t = 0
:dt:
final;
plot(t, audio{i}
);
title(wav_files(i)
.name)
xlim([0,final]);
ylim([-1,1]);
xticks(0:0.5:final);

end
    